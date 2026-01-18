package com.razdeep.konsignapi.filter;

import static com.razdeep.konsignapi.constant.KonsignConstant.CONTROLLER_API_PREFIX;

import com.razdeep.konsignapi.service.JwtUtilService;
import com.razdeep.konsignapi.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(TenantFilter.class);

    private final JwtUtilService jwtUtilService;

    public TenantFilter(JwtUtilService jwtUtilService) {
        this.jwtUtilService = jwtUtilService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = jwtUtilService.extractAccessTokenFromRequest(request);

            if (token != null) {
                String tenantId = jwtUtilService.extractTenantId(token);

                if (tenantId != null) {
                    TenantContext.setTenantId(tenantId);
                    LOG.debug("Tenant set to {}", tenantId);
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith(CONTROLLER_API_PREFIX + "/auth")
                || path.startsWith(CONTROLLER_API_PREFIX + "/health")
                || path.startsWith(CONTROLLER_API_PREFIX + "/docs");
    }
}
