package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.config.KonsignConfig;
import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.exception.UsernameAlreadyExists;
import com.razdeep.konsignapi.model.*;
import com.razdeep.konsignapi.service.AuthenticationService;
import com.razdeep.konsignapi.service.RefreshTokenService;
import com.razdeep.konsignapi.token.TokenPair;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(KonsignConstant.CONTROLLER_API_PREFIX + "/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationController(
            AuthenticationService authenticationService, RefreshTokenService refreshTokenService) {
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<KonsignApiResponse> login(
            @RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {

        TokenPair tokenPair = authenticationService.login(authenticationRequest);

        Cookie cookie = new Cookie(KonsignConstant.HEADER_REFRESH_TOKEN, tokenPair.refreshToken());
        cookie.setMaxAge(KonsignConfig.cookieMaxAge);
        cookie.setHttpOnly(KonsignConfig.cookieHttpOnly);
        cookie.setSecure(true); // MUST be true in prod
        cookie.setPath(KonsignConfig.cookiePath);
        response.addCookie(cookie);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken(tokenPair.accessToken());

        return ResponseEntity.ok(KonsignApiResponse.builder()
                .success(true)
                .data(authenticationResponse)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<KonsignApiResponse> refresh(@RequestBody RefreshRequest request) {
        TokenPair tokenPair = refreshTokenService.refresh(request.refreshToken());
        KonsignApiResponse konsignApiResponse =
                KonsignApiResponse.builder().data(tokenPair).build();
        return ResponseEntity.ok(konsignApiResponse);
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<KonsignApiResponse> signup(@RequestBody UserRegistration userRegistration)
            throws UsernameAlreadyExists {
        authenticationService.register(userRegistration);
        KonsignApiResponse konsignApiResponse = KonsignApiResponse.builder()
                .message("Successfully registered")
                .success(true)
                .build();
        return ResponseEntity.ok(konsignApiResponse);
    }

    @GetMapping(value = "/")
    public ResponseEntity<String> welcome() {
        return new ResponseEntity<>("Welcome to konsign-api", HttpStatus.OK);
    }
}
