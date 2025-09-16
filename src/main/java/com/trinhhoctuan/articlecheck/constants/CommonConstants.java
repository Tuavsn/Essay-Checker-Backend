package com.trinhhoctuan.articlecheck.constants;

import java.util.List;

public class CommonConstants {
	private CommonConstants() {
		throw new IllegalStateException("Utility class");
	}

	// ======================= Security Constants =======================

	// Public API Endpoints
	public static final String[] PUBLIC_ENDPOINTS = new String[] {
			"/actuator/health",
			"/actuator/info",
			"/actuator/prometheus",
			"/api/auth/**",
			"/oauth2/**",
			"/login/oauth2/**"
	};
	public static final String[] ADMIN_ENDPOINTS = new String[] {
			"/actuator/**",
			"/api/admin/**"
	};
	public static final String[] USER_ENDPOINTS = new String[] {
			"/api/user/**"
	};
	public static final String SUCCESS_REDIRECT_URL_SUFFIX = "/auth/callback";
	public static final String FAILURE_REDIRECT_URL_SUFFIX = "/auth/error";
	public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
	public static final String COOKIE_NAME_TO_CLEAR = "JSESSIONID";
	public static final Integer HSTS_MAX_AGE = 31536000;

	// ======================= Application Constants =======================

	// General Constants
	public static final String APPLICATION_NAME = "ArticleCheck";
	public static final String DEFAULT_LANGUAGE = "en";
	public static final String UTF_8 = "UTF-8";
	public static final String ALL_ORIGINS = "*";
	public static final String ALL_PATHS = "/**";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String ROLE_PREFIX = "ROLE_";
	public static final String GOOGLE_REGISTRATION_ID = "google";

	// Languages Supported
	public static final List<String> SUPPORTED_LANGUAGES = List.of("en", "es", "fr", "de", "zh", "ja", "ru");

	// Pagination Constants
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int MAX_PAGE_SIZE = 50;
	public static final int DEFAULT_PAGE_NUMBER = 0;
	public static final String DEFAULT_SORT_BY = "createdAt";
	public static final String DEFAULT_SORT_DIRECTION = "desc";

	// File Upload Constants
	public static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5 MB
	public static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20 MB
	public static final String[] ALLOWED_FILE_TYPES = new String[] {
			"image/jpeg",
			"image/png",
			"application/pdf"
	};

	// Header Names
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String CONTENT_TYPE_HEADER = "Content-Type";

	// Content Types
	public static final String JSON_CONTENT_TYPE = "application/json";
	public static final String XML_CONTENT_TYPE = "application/xml";
	public static final String TEXT_CONTENT_TYPE = "text/plain";
	public static final String HTML_CONTENT_TYPE = "text/html";

	// Date Formats
	public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
	public static final String SIMPLE_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// Environment profiles
	public static final String DEVELOPMENT_PROFILE = "dev";
	public static final String PRODUCTION_PROFILE = "prod";

	// Security Constants
	public static final String BEARER_PREFIX = "Bearer ";
}
