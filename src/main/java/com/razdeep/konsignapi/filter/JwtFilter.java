package com.razdeep.konsignapi.filter;

import com.razdeep.konsignapi.constant.KonsignConstant;
import com.razdeep.konsignapi.service.JwtUtilService;
import com.razdeep.konsignapi.service.KonsignUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtUtilService jwtUtilService;
    private final KonsignUserDetailsService konsignUserDetailsService;

    @Autowired
    public JwtFilter(JwtUtilService jwtUtilService, KonsignUserDetailsService konsignUserDetailsService) {
        this.jwtUtilService = jwtUtilService;
        this.konsignUserDetailsService = konsignUserDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String extractedJwtToken = null;
        try {
            extractedJwtToken = jwtUtilService.extractAccessTokenFromRequest(request);
        } catch (Exception ex) {
            ex.printStackTrace();
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails konsignUserDetails = null;
        try {
            if (!jwtUtilService.validateToken(extractedJwtToken, konsignUserDetails)) {
                filterChain.doFilter(request, response);
                return;
            }

            String extractedUsername = jwtUtilService.extractUsername(extractedJwtToken);

            konsignUserDetails = konsignUserDetailsService.loadUserByUsername(extractedUsername);

            if (konsignUserDetails == null) {
                filterChain.doFilter(request, response);
                return;
            }
        } catch (ExpiredJwtException | BadCredentialsException ex) {
            ex.printStackTrace();
            LOG.debug(ex.toString());
            String isRefreshToken = request.getHeader("isRefreshToken");
            String requestURL = request.getRequestURL().toString();
            Optional<String> refreshTokenOptional = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(KonsignConstant.HEADER_REFRESH_TOKEN))
                    .map(Cookie::getValue)
                    .findAny();
            if (refreshTokenOptional.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }
            String extractedRefreshToken = refreshTokenOptional.get();
            // allow for Refresh Token creation if following conditions are true.
            if (isRefreshToken != null
                    && isRefreshToken.equals("true")
                    && requestURL.contains("refreshtoken")
                    && jwtUtilService.validateToken(extractedRefreshToken, null)) {
                //                allowForRefreshToken(ex, request);
                // TODO: Hack fix this later
                allowForRefreshToken(null, request);
                filterChain.doFilter(request, response);
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.debug(ex.toString());
            response.sendError(401, "Most probably credentials mismatch");
            filterChain.doFilter(request, response);
            return;
        }

        if (konsignUserDetails == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(konsignUserDetails, null, konsignUserDetails.getAuthorities());

        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request, response);
    }

    private void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(null, null, null);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        //        request.setAttribute("claims", ex.getClaims());

    }
}
