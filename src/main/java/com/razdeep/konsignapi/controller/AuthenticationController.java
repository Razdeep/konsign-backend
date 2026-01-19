package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.config.KonsignConfig;
import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.exception.UsernameAlreadyExists;
import com.razdeep.konsignapi.model.*;
import com.razdeep.konsignapi.service.AuthenticationService;
import com.razdeep.konsignapi.service.JwtUtilService;
import com.razdeep.konsignapi.service.KonsignUserDetailsService;
import com.razdeep.konsignapi.service.RefreshTokenService;
import com.razdeep.konsignapi.token.TokenPair;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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
    private final RefreshTokenService refreshTokenService;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            KonsignUserDetailsService konsignUserDetailsService,
            JwtUtilService jwtUtilService,
            AuthenticationService authenticationService,
            RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.konsignUserDetailsService = konsignUserDetailsService;
        this.jwtUtilService = jwtUtilService;
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(), authenticationRequest.getPassword()));

            final UserDetails konsignUserDetails =
                    konsignUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

            final String accessToken = jwtUtilService.generateAccessToken(konsignUserDetails);

            final String refreshToken = jwtUtilService.generateRefreshToken(konsignUserDetails);

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

    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@RequestBody RefreshRequest request) {

        TokenPair response = refreshTokenService.refresh(request.getRefreshToken());

        return ResponseEntity.ok(response);
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
