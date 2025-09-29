package com.trinhhoctuan.articlecheck.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trinhhoctuan.articlecheck.config.JwtUserPrincipal;
import com.trinhhoctuan.articlecheck.utils.SecurityUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final SecurityUtil securityUtil;

  public AuthController(SecurityUtil securityUtil) {
    this.securityUtil = securityUtil;
  }

  @GetMapping("/me")
  public ResponseEntity<JwtUserPrincipal> getCurrentUserId() {
    JwtUserPrincipal userPrincipal = securityUtil.getCurrentUser();

    if (userPrincipal == null) {
      return ResponseEntity.status(401).body(null);
    }

    return ResponseEntity.ok(userPrincipal);
  }
}
