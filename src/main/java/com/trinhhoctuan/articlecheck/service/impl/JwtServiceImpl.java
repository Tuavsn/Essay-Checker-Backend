package com.trinhhoctuan.articlecheck.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.trinhhoctuan.articlecheck.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {
  @Value("${jwt.secret:mySecretKey123456789012345678901234567890}")
  private String jwtSecret;

  @Value("${jwt.expiration:86400000}") // 24 hours
  private long jwtExpirationMs;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  /**
   * Generate JWT token for authenticated user.
   *
   * @param authentication the authentication object
   * @return the generated JWT token
   */
  @Override
  public String generateToken(Authentication authentication) {
    OAuth2User userPrincipal = (OAuth2User) authentication.getPrincipal();

    String email = userPrincipal.getAttribute("email");
    Long userId = userPrincipal.getAttribute("userId");
    List<String> roles = userPrincipal.getAttribute("roles");

    Instant now = Instant.now();
    Instant expiry = now.plus(jwtExpirationMs, ChronoUnit.MILLIS);

    return Jwts.builder()
        .setSubject(email)
        .claim("userId", userId)
        .claim("roles", roles)
        .claim("email", email)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(expiry))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public String generateTokenFromUser(String email, Long userId, List<String> roles) {
    Instant now = Instant.now();
    Instant expiry = now.plus(jwtExpirationMs, ChronoUnit.MILLIS);

    return Jwts.builder()
        .setSubject(email)
        .claim("userId", userId)
        .claim("roles", roles)
        .claim("email", email)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(expiry))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  @Override
  public String getEmailFromJwtToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims.getSubject();
  }

  @Override
  public Long getUserIdFromJwtToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims.get("userId", Long.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<String> getRolesFromJwtToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return (List<String>) claims.get("roles");
  }

  @Override
  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(authToken);
      return true;
    } catch (MalformedJwtException e) {
      System.err.println("Invalid JWT token: " + e.getMessage());
    } catch (ExpiredJwtException e) {
      System.err.println("JWT token is expired: " + e.getMessage());
    } catch (UnsupportedJwtException e) {
      System.err.println("JWT token is unsupported: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.err.println("JWT claims string is empty: " + e.getMessage());
    }

    return false;
  }

  @Override
  public Map<String, Object> getAllClaimsFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims;
  }

  @Override
  public Date getExpirationDateFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

    return claims.getExpiration();
  }

  @Override
  public boolean isTokenExpired(String token) {
    Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }
}
