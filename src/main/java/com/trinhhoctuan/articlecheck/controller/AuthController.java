package com.trinhhoctuan.articlecheck.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trinhhoctuan.articlecheck.dto.LoginResponse;
import com.trinhhoctuan.articlecheck.dto.UserInfo;
import com.trinhhoctuan.articlecheck.service.JwtService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AuthController {
  private final JwtService jwtService;

  @GetMapping("/success")
  public void loginSuccess(Authentication authentication, HttpServletResponse response)
      throws IOException {

    // Tạo JWT token
    String token = jwtService.generateToken(authentication);

    // Redirect về frontend với token
    String redirectUrl = String.format("http://localhost:3000/auth/callback?token=%s", token);
    response.sendRedirect(redirectUrl);
  }

  @PostMapping("/token")
  public ResponseEntity<LoginResponse> getToken(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(401).build();
    }

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String token = jwtService.generateToken(authentication);

    LoginResponse response = LoginResponse.builder()
        .token(token)
        .tokenType("Bearer")
        .email(oAuth2User.getAttribute("email"))
        .name(oAuth2User.getAttribute("name"))
        .userId(oAuth2User.getAttribute("userId"))
        .roles(oAuth2User.getAttribute("roles"))
        .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/user")
  public ResponseEntity<UserInfo> getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(401).build();
    }

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

    UserInfo userInfo = UserInfo.builder()
        .email(oAuth2User.getAttribute("email"))
        .name(oAuth2User.getAttribute("name"))
        .userId(oAuth2User.getAttribute("userId"))
        .roles(oAuth2User.getAttribute("roles"))
        .build();

    return ResponseEntity.ok(userInfo);
  }

  @PostMapping("/validate")
  public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> request) {
    String token = request.get("token");

    if (token == null || !jwtService.validateJwtToken(token)) {
      return ResponseEntity.status(401)
          .body(Map.of("valid", false, "message", "Invalid token"));
    }

    try {
      Map<String, Object> claims = jwtService.getAllClaimsFromToken(token);
      return ResponseEntity.ok(Map.of(
          "valid", true,
          "claims", claims));
    } catch (Exception e) {
      return ResponseEntity.status(401)
          .body(Map.of("valid", false, "message", "Token validation failed"));
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refreshToken(@RequestBody Map<String, String> request) {
    String token = request.get("token");

    if (token == null || !jwtService.validateJwtToken(token)) {
      return ResponseEntity.status(401).build();
    }

    try {
      String email = jwtService.getEmailFromJwtToken(token);
      Long userId = jwtService.getUserIdFromJwtToken(token);
      List<String> roles = jwtService.getRolesFromJwtToken(token);

      String newToken = jwtService.generateTokenFromUser(email, userId, roles);

      LoginResponse response = LoginResponse.builder()
          .token(newToken)
          .tokenType("Bearer")
          .email(email)
          .userId(userId)
          .roles(roles)
          .build();

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(401).build();
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout() {
    // Trong thực tế, bạn có thể thêm token vào blacklist
    return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
  }
}
