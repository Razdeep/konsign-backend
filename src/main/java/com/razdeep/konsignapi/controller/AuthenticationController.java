package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.exception.UsernameAlreadyExists;
import com.razdeep.konsignapi.model.*;
import com.razdeep.konsignapi.service.AuthenticationService;
import com.razdeep.konsignapi.service.RefreshTokenService;
import com.razdeep.konsignapi.token.TokenPair;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
            @Valid @RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {

        TokenPair tokenPair = authenticationService.login(authenticationRequest);

        ResponseCookie cookie = ResponseCookie.from(KonsignConstant.HEADER_REFRESH_TOKEN, tokenPair.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        AuthenticationResponse authenticationResponse = new AuthenticationResponse(tokenPair.accessToken());

        return ResponseEntity.ok(KonsignApiResponse.builder()
                .success(true)
                .data(authenticationResponse)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<KonsignApiResponse> refresh(
            @CookieValue(name = "refresh-token", required = false) String refreshToken) {
        String accessToken = refreshTokenService.generateAccessTokenWithRefreshToken(refreshToken);
        KonsignApiResponse konsignApiResponse = KonsignApiResponse.builder()
                .message("new access token generated")
                .success(true)
                .data(new AuthenticationResponse(accessToken))
                .build();
        return ResponseEntity.ok(konsignApiResponse);
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<KonsignApiResponse> signup(@Valid @RequestBody UserRegistration userRegistration)
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
