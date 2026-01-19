package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.RefreshTokenEntity;
import com.razdeep.konsignapi.exception.UnauthorizedException;
import com.razdeep.konsignapi.model.KonsignUserDetails;
import com.razdeep.konsignapi.repository.RefreshTokenRepository;
import com.razdeep.konsignapi.token.RefreshTokenGenerator;
import com.razdeep.konsignapi.token.TokenPair;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    public TokenPair refresh(String refreshTokenValue) {

        RefreshTokenEntity oldToken = repository
                .findByToken(refreshTokenValue)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (oldToken.isRevoked()) {
            throw new UnauthorizedException("Refresh token revoked");
        }

        if (oldToken.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired");
        }

        // Rotate token (important!)
        oldToken.setRevoked(true);
        repository.save(oldToken);

        RefreshTokenEntity newToken = new RefreshTokenEntity();
        newToken.setToken(generator.generate());
        newToken.setUserId(oldToken.getUserId());
        newToken.setTenantId(oldToken.getTenantId());
        newToken.setDeviceId(oldToken.getDeviceId());
        newToken.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));

        repository.save(newToken);

        final UserDetails konsignUserDetails = konsignUserDetailsService.loadUserByUserId(oldToken.getUserId());

        // Create new access token (JWT)
        String accessToken = jwtUtilService.generateAccessToken(konsignUserDetails);

        return new TokenPair(accessToken, newToken.getToken());
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
