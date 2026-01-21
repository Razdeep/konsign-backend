package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.RefreshTokenEntity;
import com.razdeep.konsignapi.exception.UnauthorizedException;
import com.razdeep.konsignapi.model.KonsignUserDetails;
import com.razdeep.konsignapi.repository.RefreshTokenRepository;
import com.razdeep.konsignapi.token.RefreshTokenGenerator;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final RefreshTokenGenerator generator;
    private final JwtUtilService jwtUtilService;
    private final KonsignUserDetailsService konsignUserDetailsService;

    public RefreshTokenService(
            RefreshTokenRepository repository,
            RefreshTokenGenerator generator,
            JwtUtilService jwtUtilService,
            KonsignUserDetailsService userDetailsService) {
        this.repository = repository;
        this.generator = generator;
        this.jwtUtilService = jwtUtilService;
        this.konsignUserDetailsService = userDetailsService;
    }

    public String generateAccessTokenWithRefreshToken(String refreshTokenValue) {

        RefreshTokenEntity oldToken = repository
                .findByToken(refreshTokenValue)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (oldToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token revoked");
        }

        if (oldToken.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired");
        }

        final UserDetails konsignUserDetails = konsignUserDetailsService.loadUserByUserId(oldToken.getUserId());

        return jwtUtilService.generateAccessToken(konsignUserDetails);
    }

    public void registerRefreshToken(KonsignUserDetails konsignUserDetails, String refreshTokenValue) {
        repository.save(RefreshTokenEntity.builder()
                .token(refreshTokenValue)
                .userId(konsignUserDetails.getId())
                .tenantId(konsignUserDetails.getTenantId())
                .expiresAt(new Date(System.currentTimeMillis() + jwtUtilService.getRefreshExpirationInMillis())
                        .toInstant())
                .revoked(false)
                .build());
    }
}
