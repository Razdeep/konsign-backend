package com.razdeep.konsignapi.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtUtilService {

    private static final String BEARER_KEYWORD = "Bearer ";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.jwtExpirationInMillis}")
    private int jwtExpirationInMillis;

    @Value("${jwt.refreshTokenExpirationInMillis}")
    private int refreshExpirationInMillis;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails konsignUserDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, konsignUserDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMillis))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean validateToken(String token, UserDetails konsignUserDetails) throws ExpiredJwtException {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
        } catch (ExpiredJwtException ex) {
            throw ex;
        }
    }

    public String doGenerateRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationInMillis))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Map<String, Object> getMapFromIoJsonWebTokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        if (claims == null) {
            return expectedMap;
        }
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

    public String extractAccessTokenFromRequest(HttpServletRequest request) {
        String authorizationHeaderStr = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeaderStr == null || !authorizationHeaderStr.startsWith(BEARER_KEYWORD)) {
            throw new JwtException("jwt not found");
        }

        return authorizationHeaderStr.substring(BEARER_KEYWORD.length());
    }
}
