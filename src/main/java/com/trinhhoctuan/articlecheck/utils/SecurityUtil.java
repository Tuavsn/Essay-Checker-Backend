package com.trinhhoctuan.articlecheck.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.trinhhoctuan.articlecheck.config.JwtUserPrincipal;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SecurityUtil {
  public static JwtUserPrincipal getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof JwtUserPrincipal jwtUserPrincipal) {
      return jwtUserPrincipal;
    }
    return null;
  }
}
