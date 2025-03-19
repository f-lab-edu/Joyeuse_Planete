package com.f_lab.joyeuse_planete.core.security.cookie;

import com.f_lab.joyeuse_planete.core.security.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;


public class CookieUtil {

  @Value("${cookie.domain:.localhost}")
  private String DOMAIN;

  @Value("${cookie.path:/}")
  private String PATH;
  private static final String REFRESH_TOKEN_KEY = "RefreshToken";


  public void setRefreshTokenAsCookie(HttpServletResponse response, String refreshToken) {
    Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, refreshToken);

    cookie.setDomain(DOMAIN);
    cookie.setPath(PATH);
    cookie.setMaxAge(JwtUtil.JWT_EXPIRATION_DATE_REFRESH);
    cookie.setHttpOnly(true);

    response.addCookie(cookie);
  }

  public String getRefreshTokenFromCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();

    if (cookies == null)
      return null;

    return Arrays.stream(cookies)
        .filter(c -> c.getName().equals(REFRESH_TOKEN_KEY))
        .map(Cookie::getValue)
        .findAny()
        .orElse(null);
  }

  public void deleteCookie(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();

    if (cookies != null) {
      Arrays.stream(cookies)
          .filter(c -> c.getName().equals(REFRESH_TOKEN_KEY))
          .findFirst()
          .ifPresent(c -> {
            c.setDomain(DOMAIN);
            c.setPath(PATH);
            c.setMaxAge(0);

            response.addCookie(c);
          });
    }
  }
}
