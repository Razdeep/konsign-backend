package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.KonsignUser;
import com.razdeep.konsignapi.exception.UsernameAlreadyExists;
import com.razdeep.konsignapi.model.AuthenticationRequest;
import com.razdeep.konsignapi.model.KonsignUserDetails;
import com.razdeep.konsignapi.model.UserRegistration;
import com.razdeep.konsignapi.repository.KonsignUserRepository;
import com.razdeep.konsignapi.token.TokenPair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final KonsignUserRepository konsignUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AuthenticationManager authenticationManager;
    private final KonsignUserDetailsService konsignUserDetailsService;
    private final JwtUtilService jwtUtilService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(
            KonsignUserRepository konsignUserRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            AuthenticationManager authenticationManager,
            KonsignUserDetailsService konsignUserDetailsService,
            JwtUtilService jwtUtilService,
            RefreshTokenService refreshTokenService) {
        this.konsignUserRepository = konsignUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.konsignUserDetailsService = konsignUserDetailsService;
        this.jwtUtilService = jwtUtilService;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(UserRegistration userRegistration) throws UsernameAlreadyExists {
        if (konsignUserRepository
                .findKonsignUserByUsername(userRegistration.getUsername())
                .isPresent()) {
            throw new UsernameAlreadyExists();
        }
        KonsignUser konsignUser = new KonsignUser(userRegistration);
        konsignUser.setPassword(bCryptPasswordEncoder.encode(konsignUser.getPassword()));
        konsignUserRepository.save(konsignUser);
    }

    public TokenPair login(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.username(), authenticationRequest.password()));

        final KonsignUserDetails konsignUserDetails =
                konsignUserDetailsService.loadUserByUsername(authenticationRequest.username());

        final String accessToken = jwtUtilService.generateAccessToken(konsignUserDetails);

        final String refreshToken = jwtUtilService.generateRefreshToken();

        refreshTokenService.registerRefreshToken(konsignUserDetails, refreshToken);

        return new TokenPair(accessToken, refreshToken);
    }
}
