package com.trinhhoctuan.articlecheck.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.trinhhoctuan.articlecheck.config.JwtUserPrincipal;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for security-related operations.
 * Provides methods to retrieve the current authenticated user and their ID.
 * Integrates with Spring Security's context to access authentication details.
 */
@Component
@Slf4j
public class SecurityUtil {
  /**
   * Get the currently authenticated user principal.
   * 
   * @return
   */
  public JwtUserPrincipal getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal jwtUserPrincipal) {
      return jwtUserPrincipal;
    }
    return null;
  }

  /**
   * Get the ID of the currently authenticated user.
   * 
   * @return
   */
  public Long getCurrentUserId() {
    JwtUserPrincipal currentUser = getCurrentUser();
    if (currentUser != null) {
      return currentUser.getId();
    }
    return null;
  }
}
