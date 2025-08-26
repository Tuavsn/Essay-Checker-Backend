package com.trinhhoctuan.articlecheck.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.trinhhoctuan.articlecheck.model.Role;
import com.trinhhoctuan.articlecheck.model.User;
import com.trinhhoctuan.articlecheck.repository.RoleRepository;
import com.trinhhoctuan.articlecheck.repository.UserRepository;
import com.trinhhoctuan.articlecheck.service.CustomOAuth2UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService implements CustomOAuth2UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  /**
   * Load user information from OAuth2 provider
   * 
   * @param userRequest
   * @return
   */
  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);

    String email = oauth2User.getAttribute("email");
    String name = oauth2User.getAttribute("name");
    String googleId = oauth2User.getAttribute("sub");

    // Find or Create user in database
    User user = userRepository.findByEmail(email)
        .orElseGet(() -> createNewUser(email, name, googleId));

    // Update user information if needed
    updateUserInfo(user, name, googleId);

    // Create authorities from roles
    Collection<SimpleGrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
        .collect(Collectors.toList());

    // Create custom attributes
    Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
    attributes.put("userId", user.getId());
    attributes.put("roles", user.getRoles().stream()
        .map(Role::getName)
        .collect(Collectors.toList()));

    return new DefaultOAuth2User(authorities, attributes, "email");
  }

  /**
   * Create new user
   * 
   * @param email
   * @param name
   * @param googleId
   * @return
   */
  private User createNewUser(String email, String name, String googleId) {
    User user = new User();
    user.setEmail(email);
    user.setName(name);
    user.setGoogleId(googleId);
    user.setEnabled(true);

    // Assign default role as USER
    Role userRole = roleRepository.findByName("USER")
        .orElseGet(() -> createDefaultRole("USER"));
    user.setRoles(Set.of(userRole));

    return userRepository.save(user);
  }

  /**
   * Update user information
   * 
   * @param user
   * @param name
   * @param googleId
   */
  private void updateUserInfo(User user, String name, String googleId) {
    boolean updated = false;

    if (!Objects.equals(user.getName(), name)) {
      user.setName(name);
      updated = true;
    }

    if (!Objects.equals(user.getGoogleId(), googleId)) {
      user.setGoogleId(googleId);
      updated = true;
    }

    if (updated) {
      userRepository.save(user);
    }
  }

  /**
   * Create a new role with the given name.
   * 
   * @param roleName
   * @return
   */
  private Role createDefaultRole(String roleName) {
    Role role = new Role();
    role.setName(roleName);
    return roleRepository.save(role);
  }
}
