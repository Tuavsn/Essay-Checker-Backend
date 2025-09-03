package com.trinhhoctuan.articlecheck.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;

// OAuth2 imports removed - using Spring Boot auto-configuration
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.trinhhoctuan.articlecheck.service.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;

  private static final String[] PUBLIC_ENDPOINTS = new String[] {
      "/",
      "/api/public/**",
      "/actuator/health",
      "/actuator/info",
      "/actuator/prometheus"
  };

  private static final String[] ADMIN_ENDPOINTS = new String[] {
      "/actuator/**",
      "/api/admin/**"
  };

  private static final String[] USER_ENDPOINTS = new String[] {
      "/api/user/**"
  };

  private static final List<String> EXPOSE_HEADERS = List.of(
      "Authorization",
      "Content-Type",
      "X-Total-Count",
      "X-Page-Number",
      "X-Page-Size");

  @Value("${app.cors.allowed-origins}")
  private String allowedOrigins;

  @Value("${app.cors.allowed-methods}")
  private String allowedMethods;

  @Value("${app.cors.allowed-headers}")
  private String allowedHeaders;

  @Value("${app.cors.allow-credentials}")
  private boolean allowCredentials;

  @Value("${app.cors.max-age}")
  private long maxAge;

  @Bean
  public SecurityFilterChain productionSecurityFilterChain(HttpSecurity http) throws Exception {
    return http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // Public endpoints
            .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
            // Admin endpoints
            .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
            // User endpoints
            .requestMatchers(USER_ENDPOINTS).hasRole("USER")
            // All other endpoints require authentication
            .anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2
            .defaultSuccessUrl("http://localhost:3000/dashboard", true)
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService)))
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.sameOrigin())
            .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                .maxAgeInSeconds(31536000)
                .includeSubDomains(true))
            .contentTypeOptions(Customizer.withDefaults())
            .referrerPolicy(Customizer.withDefaults()))
        .logout(logout -> logout
            .logoutSuccessUrl("/")
            .deleteCookies("JSESSIONID"))
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Parse allowed origins
    if ("*".equals(allowedOrigins)) {
      configuration.setAllowedOrigins(List.of("*"));
    } else {
      List<String> origins = Arrays.asList(allowedOrigins.split(","));
      configuration.setAllowedOrigins(origins);
    }

    // Parse allowed methods
    if ("*".equals(allowedMethods)) {
      configuration.setAllowedMethods(List.of("*"));
    } else {
      List<String> methods = Arrays.asList(allowedMethods.split(","));
      configuration.setAllowedMethods(methods);
    }

    // Parse allowed headers
    if ("*".equals(allowedHeaders)) {
      configuration.setAllowedHeaders(List.of("*"));
    } else {
      List<String> headers = Arrays.asList(allowedHeaders.split(","));
      configuration.setAllowedHeaders(headers);
    }

    configuration.setAllowCredentials(allowCredentials);
    configuration.setMaxAge(maxAge);

    // Expose common headers
    configuration.setExposedHeaders(EXPOSE_HEADERS);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
