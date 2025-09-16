package com.trinhhoctuan.articlecheck.utils;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for handling cookies
 */
@Component
@Slf4j
public class CookieUtil {
    /**
     * Get a cookie by name
     * 
     * @param request
     * @param name
     * @return
     */
    public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null)
            return Optional.empty();
        return Arrays.stream(request.getCookies()).filter(cookie -> name.equals(cookie.getName())).findFirst();
    }

    /**
     * Add a cookie to the response
     * 
     * @param response
     * @param name
     * @param value
     * @param maxAge
     */
    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * Delete a cookie by name
     * 
     * @param request
     * @param response
     * @param name
     */
    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Optional<Cookie> cookieOpt = getCookie(request, name);
        if (cookieOpt.isPresent()) {
            Cookie cookie = cookieOpt.get();
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }

    /**
     * Serialize a cookie to a Base64 string
     * 
     * @param cookie
     * @return
     */
    public String serialize(Cookie cookie) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(cookie));
    }

    /**
     * Deserialize a cookie from a Base64 string
     * 
     * @param <T>
     * @param cookie
     * @param cls
     * @return
     */
    public <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
