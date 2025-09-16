package com.trinhhoctuan.articlecheck.config;

import java.security.Principal;
import java.util.Set;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JwtUserPrincipal implements Principal {
  private final Long id;
  private final String email;
  private final String username;
  private final Set<String> roles;

  @Override
  public String getName() {
    return email;
  }
}
