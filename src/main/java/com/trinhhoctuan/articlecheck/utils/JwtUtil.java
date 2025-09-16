package com.trinhhoctuan.articlecheck.utils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import com.trinhhoctuan.articlecheck.constants.JwtClaims;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT Utility class for generating and validating JWT tokens
 */
@Component
@Slf4j
public class JwtUtil {
    private final SecretKey secretKey;
    private final Long expiration;
    private final Long refreshExpiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") Long expiration,
            @Value("${jwt.refresh-expiration}") Long refreshExpiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String generateAccessToken(Authentication authentication) {
        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();

        String subject = user.getAttribute(JwtClaims.EMAIL);

        Map<String, Object> claims = Map.of(
                JwtClaims.TYPE, JwtClaims.ACCESS_TOKEN,
                JwtClaims.ID, user.getAttribute(JwtClaims.ID),
                JwtClaims.USERNAME, user.getAttribute(JwtClaims.USERNAME),
                JwtClaims.PICTURE, user.getAttribute(JwtClaims.PICTURE),
                JwtClaims.ROLES, authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));

        return generateToken(subject, claims, expiration);
    }

    public String generateRefreshToken(Authentication authentication) {
        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();

        String subject = user.getAttribute(JwtClaims.EMAIL);

        Map<String, Object> claims = Map.of(
                JwtClaims.TYPE, JwtClaims.REFRESH_TOKEN,
                JwtClaims.USERNAME, user.getAttribute(JwtClaims.USERNAME));

        return generateToken(subject, claims, refreshExpiration);
    }

    private String generateToken(String subject, Map<String, Object> claims, Long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(System.currentTimeMillis() + expirationTime);
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean isAccessToken(String token) {
        return JwtClaims.ACCESS_TOKEN.equals(extractTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return JwtClaims.REFRESH_TOKEN.equals(extractTokenType(token));
    }

    public Long extractId(String token) {
        return extractClaims(token).get(JwtClaims.ID, Long.class);
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractUsername(String token) {
        return extractClaims(token).get(JwtClaims.USERNAME, String.class);
    }

    @SuppressWarnings("unchecked")
    public Set<String> extractRoles(String token) {
        List<String> roles = extractClaims(token).get(JwtClaims.ROLES, List.class);
        return new HashSet<>(roles);
    }

    private String extractTokenType(String token) {
        return extractClaims(token).get(JwtClaims.TYPE, String.class);
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT token is malformed: {}", e.getMessage());
        } catch (SecurityException | IllegalArgumentException e) {
            log.error("JWT token validation failed: {}", e.getMessage());
        }
        return false;
    }
}
