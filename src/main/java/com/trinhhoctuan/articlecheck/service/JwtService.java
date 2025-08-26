package com.trinhhoctuan.articlecheck.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;


public interface JwtService {
  public String generateToken(Authentication authentication);

  public String generateTokenFromUser(String email, Long userId, List<String> roles);

  public String getEmailFromJwtToken(String token);

  public Long getUserIdFromJwtToken(String token);

  public List<String> getRolesFromJwtToken(String token);

  public boolean validateJwtToken(String authToken);

  public Map<String, Object> getAllClaimsFromToken(String token);

  public Date getExpirationDateFromToken(String token);

  public boolean isTokenExpired(String token);
}
