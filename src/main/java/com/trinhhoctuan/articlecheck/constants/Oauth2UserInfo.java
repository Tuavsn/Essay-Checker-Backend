package com.trinhhoctuan.articlecheck.constants;

import java.util.Map;

import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

public final class Oauth2UserInfo {
  public static final String ID = "id";
  public static final String EMAIL = "email";
  public static final String USERNAME = "name";
  public static final String PICTURE = "picture";
  public static final String GOOGLE_ID = "sub";
  public static final String ROLE = "role";
  public static final String LOCALE = "locale";

  private Oauth2UserInfo() {}

  public static Map<String, Object> getOauth2UserInfo(DefaultOAuth2User oAuth2User) {
    return Map.of(
        Oauth2UserInfo.EMAIL, oAuth2User.getAttribute(Oauth2UserInfo.EMAIL),
        Oauth2UserInfo.USERNAME, oAuth2User.getAttribute(Oauth2UserInfo.USERNAME),
        Oauth2UserInfo.PICTURE, oAuth2User.getAttribute(Oauth2UserInfo.PICTURE),
        Oauth2UserInfo.ROLE, oAuth2User.getAttribute(Oauth2UserInfo.ROLE));
  }
}
