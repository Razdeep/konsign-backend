package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.KonsignUser;
import com.razdeep.konsignapi.exception.UsernameAlreadyExists;
import com.razdeep.konsignapi.model.AuthenticationRequest;
import com.razdeep.konsignapi.model.UserRegistration;
import com.razdeep.konsignapi.repository.KonsignUserRepository;
import com.razdeep.konsignapi.token.TokenPair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final KonsignUserRepository konsignUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AuthenticationManager authenticationManager;
    private final KonsignUserDetailsService konsignUserDetailsService;
    private final JwtUtilService jwtUtilService;

    public AuthenticationService(
            KonsignUserRepository konsignUserRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            AuthenticationManager authenticationManager,
            KonsignUserDetailsService konsignUserDetailsService,
            JwtUtilService jwtUtilService) {
        this.konsignUserRepository = konsignUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.konsignUserDetailsService = konsignUserDetailsService;
        this.jwtUtilService = jwtUtilService;
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
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        final UserDetails konsignUserDetails =
                konsignUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String accessToken = jwtUtilService.generateAccessToken(konsignUserDetails);

        final String refreshToken = jwtUtilService.generateRefreshToken(konsignUserDetails);

        return new TokenPair(accessToken, refreshToken);
    }
}
