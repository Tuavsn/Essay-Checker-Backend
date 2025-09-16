package com.trinhhoctuan.articlecheck.enums;

import com.trinhhoctuan.articlecheck.constants.CommonConstants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
  USER("USER"),
  ADMIN("ADMIN");

  private final String name;

  public String getAuthority() {
    return CommonConstants.ROLE_PREFIX + this.name();
  } 
}