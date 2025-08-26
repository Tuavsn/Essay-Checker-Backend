package com.trinhhoctuan.articlecheck.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
  private String token;
  private String tokenType;
  private String email;
  private String name;
  private Long userId;
  private List<String> roles;
}