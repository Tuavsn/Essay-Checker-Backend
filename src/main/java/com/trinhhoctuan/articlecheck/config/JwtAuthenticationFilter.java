package com.trinhhoctuan.articlecheck.config;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.trinhhoctuan.articlecheck.constants.CommonConstants;
import com.trinhhoctuan.articlecheck.utils.JwtUtil;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher;

    public JwtAuthenticationFilter(
            JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.pathMatcher = new AntPathMatcher();
    }

    @Override
    public void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain) throws IOException, ServletException {
        try {
            // Get JWT from request header
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtUtil.validateJwtToken(jwt) && jwtUtil.isAccessToken(jwt)) {
                Long id = jwtUtil.extractId(jwt);
                String email = jwtUtil.extractEmail(jwt);
                String username = jwtUtil.extractUsername(jwt);
                Set<String> roles = jwtUtil.extractRoles(jwt);

                var authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                JwtUserPrincipal jwtPrincipal = new JwtUserPrincipal(id, email, username, roles);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        jwtPrincipal, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Successfully authenticated user: {} with JWT", email);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(CommonConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(CommonConstants.BEARER_PREFIX)) {
            return bearerToken.substring(CommonConstants.BEARER_PREFIX.length());
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        for (String pattern : CommonConstants.PUBLIC_ENDPOINTS) {
            if (pathMatcher.match(pattern, path)) {
                return true; // skip filter
            }
        }
        return false;
    }
}
