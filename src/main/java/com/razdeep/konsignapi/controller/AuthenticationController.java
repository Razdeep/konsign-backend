package com.razdeep.konsignapi.controller;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.exception.UsernameAlreadyExists;
import com.razdeep.konsignapi.model.*;
import com.razdeep.konsignapi.service.AuthenticationService;
import com.razdeep.konsignapi.service.RefreshTokenService;
import com.razdeep.konsignapi.token.TokenPair;
import jakarta.servlet.http.HttpServletResponse;
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
            @RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {

        TokenPair tokenPair = authenticationService.login(authenticationRequest);

        ResponseCookie cookie = ResponseCookie.from(KonsignConstant.HEADER_REFRESH_TOKEN, tokenPair.refreshToken())
                .httpOnly(false) // TODO hack
                .secure(false) // TODO make it secure in prod / have local false setup
                .sameSite("Lax") // TODO later change to strict for prod / make it config driven
                .path("/")
                .build();

        //        response.addCookie(cookie);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken(tokenPair.accessToken());

        return ResponseEntity.ok(KonsignApiResponse.builder()
                .success(true)
                .data(authenticationResponse)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<KonsignApiResponse> refresh(
            @CookieValue(name = "refresh-token", required = false) String refreshToken) {
        TokenPair tokenPair = refreshTokenService.refresh(refreshToken);
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
