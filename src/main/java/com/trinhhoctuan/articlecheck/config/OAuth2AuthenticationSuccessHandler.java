package com.trinhhoctuan.articlecheck.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trinhhoctuan.articlecheck.constants.CommonConstants;
import com.trinhhoctuan.articlecheck.constants.Oauth2UserInfo;
import com.trinhhoctuan.articlecheck.utils.CookieUtil;
import com.trinhhoctuan.articlecheck.utils.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles successful OAuth2 authentication by generating a JWT token and
 * redirecting the user
 * to the client application with the token and user info as query parameters.
 */
@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtUtil jwtUtil;
	private final CookieUtil cookieUtil;
	private final ObjectMapper objectMapper;
	private final Long refreshExpiration;
	private final String authorizedRedirectUri;
	private final String TOKEN_QUERY_PARAM = "token";
	private final String USER_INFO_QUERY_PARAM = "userInfo";

	public OAuth2AuthenticationSuccessHandler(
			JwtUtil jwtUtil,
			CookieUtil cookieUtil,
			ObjectMapper objectMapper,
			@Value("${app.jwt.refresh-expiration}") Long refreshExpiration,
			@Value("${app.oauth.authorizedRedirectUri}") String authorizedRedirectUri) {
		this.jwtUtil = jwtUtil;
		this.cookieUtil = cookieUtil;
		this.objectMapper = objectMapper;
		this.refreshExpiration = refreshExpiration;
		this.authorizedRedirectUri = authorizedRedirectUri;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		clearAuthenticationAttributes(request);

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		// Generate JWT token
		String token = jwtUtil.generateAccessToken(authentication);
		String refreshToken = jwtUtil.generateRefreshToken(authentication);

		// Save refresh token in HttpOnly cookie
		cookieUtil.addCookie(response, CommonConstants.REFRESH_TOKEN_COOKIE_NAME, refreshToken,
				refreshExpiration.intValue() / 1000);

		// Remove JSESSIONID;
		cookieUtil.deleteCookie(request, response, CommonConstants.COOKIE_NAME_TO_CLEAR);

		// Get User Info
		DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

		Map<String, Object> userInfo = Oauth2UserInfo.getOauth2UserInfo(oAuth2User);

		try {
			// Convert User Info to JSON and URL Encode it
			String userInfoJson = objectMapper.writeValueAsString(userInfo);
			String encodedUserInfo = URLEncoder.encode(userInfoJson, StandardCharsets.UTF_8);

			// Build the redirect URL
			return UriComponentsBuilder.fromUriString(authorizedRedirectUri + CommonConstants.SUCCESS_REDIRECT_URL_SUFFIX)
					.queryParam(TOKEN_QUERY_PARAM, token)
					.queryParam(USER_INFO_QUERY_PARAM, encodedUserInfo)
					.build()
					.toUriString();

		} catch (Exception e) {
			log.error("Error creating redirect URL", e);
			return authorizedRedirectUri + CommonConstants.FAILURE_REDIRECT_URL_SUFFIX;
		}
	}
}
