package com.trinhhoctuan.articlecheck.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.trinhhoctuan.articlecheck.constants.CommonConstants;
import com.trinhhoctuan.articlecheck.constants.Oauth2UserInfo;
import com.trinhhoctuan.articlecheck.enums.Role;
import com.trinhhoctuan.articlecheck.models.User;
import com.trinhhoctuan.articlecheck.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  private final UserRepository userRepository;

  public CustomOAuth2UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    try {
      return processOauth2User(userRequest, oAuth2User);
    } catch (Exception e) {
      log.error("Error processing OAuth2 user", e);
      throw new OAuth2AuthenticationException("Error processing OAuth2 user: " + e.getMessage());
    }
  }

  private OAuth2User processOauth2User(OAuth2UserRequest request, OAuth2User oAuth2User) {
    String registrationId = request.getClientRegistration().getRegistrationId();

    if (!CommonConstants.GOOGLE_REGISTRATION_ID.equals(registrationId)) {
      throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
    }

    String googleId = oAuth2User.getAttribute(Oauth2UserInfo.GOOGLE_ID);
    String email = oAuth2User.getAttribute(Oauth2UserInfo.EMAIL);
    String username = oAuth2User.getAttribute(Oauth2UserInfo.USERNAME);
    String picture = oAuth2User.getAttribute(Oauth2UserInfo.PICTURE);
    String locale = oAuth2User.getAttribute(Oauth2UserInfo.LOCALE);

    if (email == null || email.isEmpty()) {
      throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
    }

    Optional<User> optionalUser = userRepository.findByEmail(email);

    User user;

    if (optionalUser.isPresent()) {
      user = optionalUser.get();
      updateUserInfo(googleId, user, username, picture, locale);
    } else {
      user = createNewUser(googleId, email, username, picture, locale);
    }

    Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

    Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
    attributes.put(Oauth2UserInfo.ID, user.getId());
    attributes.put(Oauth2UserInfo.ROLE, authorities);

    return new DefaultOAuth2User(authorities, attributes, Oauth2UserInfo.EMAIL);
  }

  private User createNewUser(String googleId, String email, String username, String picture, String locale) {
    return userRepository.save(User.builder()
        .email(email)
        .username(username)
        .googleId(googleId)
        .picture(picture)
        .role(Role.USER)
        .locale(locale)
        .build());
  }

  private void updateUserInfo(String googleId, User user, String username, String picture, String locale) {
    boolean updated = false;

    if (!Objects.equals(user.getUsername(), username)) {
      user.setUsername(username);
      updated = true;
    }

    if (!Objects.equals(user.getPicture(), picture)) {
      user.setPicture(picture);
      updated = true;
    }

    if (!Objects.equals(user.getGoogleId(), googleId)) {
      user.setGoogleId(googleId);
      updated = true;
    }

    if (!Objects.equals(user.getLocale(), locale)) {
      user.setLocale(locale);
      updated = true;
    }

    if (updated) {
      userRepository.save(user);
    }
  }
}
