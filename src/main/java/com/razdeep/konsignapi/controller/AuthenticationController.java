package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.config.KonsignConfig;
import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.exception.UsernameAlreadyExists;
import com.razdeep.konsignapi.model.*;
import com.razdeep.konsignapi.service.AuthenticationService;
import com.razdeep.konsignapi.service.JwtUtilService;
import com.razdeep.konsignapi.service.KonsignUserDetailsService;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(KonsignConstant.CONTROLLER_API_PREFIX + "/auth")
public class AuthenticationController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;
    private final KonsignUserDetailsService konsignUserDetailsService;
    private final JwtUtilService jwtUtilService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            KonsignUserDetailsService konsignUserDetailsService,
            JwtUtilService jwtUtilService,
            AuthenticationService authenticationService) {
        this.authenticationManager = authenticationManager;
        this.konsignUserDetailsService = konsignUserDetailsService;
        this.jwtUtilService = jwtUtilService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));

            final UserDetails konsignUserDetails =
                    konsignUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

            final String tenantId = ((KonsignUserDetails) konsignUserDetails).getTenantId();

            final String accessToken = jwtUtilService.generateToken(konsignUserDetails);

            final Map<String, Object> claims = new HashMap<>();
            claims.put("tenantId", tenantId);

            final String refreshToken = jwtUtilService.doGenerateRefreshToken(claims, konsignUserDetails.getUsername());

            Cookie cookie = new Cookie(KonsignConstant.HEADER_REFRESH_TOKEN, refreshToken);
            cookie.setMaxAge(KonsignConfig.cookieMaxAge);
            cookie.setHttpOnly(KonsignConfig.cookieHttpOnly);
            cookie.setSecure(true); // MUST be true in prod
            cookie.setPath(KonsignConfig.cookiePath);
            response.addCookie(cookie);

            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setAccessToken(accessToken);

            return ResponseEntity.ok(authenticationResponse);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Username or password mismatch");

        } catch (Exception e) {
            LOG.error("Login failed for user {}: {}", authenticationRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
    }

    @GetMapping(value = "/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {

        if (request.getCookies() == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<String> refreshTokenOptional = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(KonsignConstant.HEADER_REFRESH_TOKEN))
                .map(Cookie::getValue)
                .findAny();

        if (refreshTokenOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String refreshToken = refreshTokenOptional.get();
        LOG.info("refresh token: %s".formatted(refreshToken));

        try {
            if (jwtUtilService.validateToken(refreshToken, null)) {
                //
                // jwtUtilService.validateToken(jwtUtilService.extractAccessTokenFromRequest(request),
                // null);
                DefaultClaims claims = (DefaultClaims) request.getAttribute(KonsignConstant.HEADER_CLAIMS);
                final var claimsMap = jwtUtilService.getMapFromIoJsonWebTokenClaims(claims);
                String jwtToken = jwtUtilService.doGenerateRefreshToken(claimsMap, (String) claimsMap.get("sub"));
                AuthenticationResponse authenticationResponse = new AuthenticationResponse();
                authenticationResponse.setAccessToken(jwtToken);
                return ResponseEntity.ok(authenticationResponse);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<ResponseVerdict> signup(@RequestBody UserRegistration userRegistration) {
        ResponseVerdict responseVerdict = new ResponseVerdict();
        try {
            authenticationService.register(userRegistration);
        } catch (UsernameAlreadyExists e) {
            responseVerdict.setMessage("User already exists");
            return new ResponseEntity<>(responseVerdict, HttpStatus.BAD_REQUEST);
        }
        responseVerdict.setMessage("Successfully registered");
        return new ResponseEntity<>(responseVerdict, HttpStatus.OK);
    }

    @GetMapping(value = "/")
    public ResponseEntity<String> welcome() {
        return new ResponseEntity<>("Welcome to konsign-api", HttpStatus.OK);
    }
}
