package com.trinhhoctuan.articlecheck.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.trinhhoctuan.articlecheck.constants.CommonConstants;
import com.trinhhoctuan.articlecheck.enums.Role;

/**
 * Security Configuration
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(
			OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
			CustomOAuth2UserService customOAuth2UserService,
			JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
		this.customOAuth2UserService = customOAuth2UserService;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	/**
	 * Security Configuration
	 * 
	 * @param http
	 * @return
	 * @throws Exception
	 */
	@Bean
	public SecurityFilterChain productionSecurityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						// Public endpoints
						.requestMatchers(CommonConstants.PUBLIC_ENDPOINTS).permitAll()
						// Admin endpoints
						.requestMatchers(CommonConstants.ADMIN_ENDPOINTS).hasRole(Role.ADMIN.getName())
						// User endpoints
						.requestMatchers(CommonConstants.USER_ENDPOINTS).hasRole(Role.USER.getName())
						// All other endpoints require authentication
						.anyRequest().authenticated())
				.oauth2Login(oauth2 -> oauth2
						.userInfoEndpoint(userInfo -> userInfo
								.userService(customOAuth2UserService))
						.successHandler(oAuth2AuthenticationSuccessHandler)
						.failureHandler(null))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.headers(headers -> headers
						.frameOptions(frameOptions -> frameOptions.sameOrigin())
						.httpStrictTransportSecurity(hstsConfig -> hstsConfig
								.maxAgeInSeconds(CommonConstants.HSTS_MAX_AGE)
								.includeSubDomains(true))
						.contentTypeOptions(Customizer.withDefaults())
						.referrerPolicy(Customizer.withDefaults()))
				.logout(logout -> logout
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.deleteCookies(CommonConstants.REFRESH_TOKEN_COOKIE_NAME))
				.build();
	}
}
