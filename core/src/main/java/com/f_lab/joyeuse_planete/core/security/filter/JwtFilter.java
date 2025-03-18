package com.f_lab.joyeuse_planete.core.security.filter;

import com.f_lab.joyeuse_planete.core.domain.MemberRole;
import com.f_lab.joyeuse_planete.core.domain.RefreshToken;
import com.f_lab.joyeuse_planete.core.domain.repository.RefreshTokenRepository;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.security.cookie.CookieUtil;
import com.f_lab.joyeuse_planete.core.security.jwt.JwtUtil;
import com.f_lab.joyeuse_planete.core.security.jwt.JwtUtil.Payload;
import com.f_lab.joyeuse_planete.core.util.log.LogUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  private final CookieUtil cookieUtil;

  private final RefreshTokenRepository refreshTokenRepository;
  private static final String GENERAL_TOKEN_PREFIX = "Bearer ";

/**
 *  case1: access token 과 refresh token 모두가 만료된 경우 → 에러 발생 (재 로그인하여 둘다 새로 발급)
 *  case2: access token 은 만료됐지만, refresh token 은 유효한 경우 → refresh token 을 검증하여 access token 재발급
 *  case3: access token 은 유효하지만, refresh token 은 만료된 경우 → access token 을 검증하여 refresh token 재발급
 *  case4: access token 과 refresh token 모두가 유효한 경우 → 정상 처리
 */
  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
    try {
      String accessToken = extractAccessToken(req);
      String refreshToken = cookieUtil.getRefreshTokenFromCookie(req);

      if (StringUtils.hasText(accessToken) && StringUtils.hasText(refreshToken)) {

        // Access Token 과 Refresh Token 모두가 유효한 경우 → 정상 처리
        if (!jwtUtil.hasExpired(accessToken) && !jwtUtil.hasExpired(refreshToken))
          processForValidToken(accessToken, refreshToken);

        // Access Token 이 만료되었지만 Refresh Token 은 유효한 경우 → Refresh Token 을 검증하여 Access Token 재발급
        else if (jwtUtil.hasExpired(accessToken) && !jwtUtil.hasExpired(refreshToken))
          processForExpiredAccessTokenAndInDateRefreshToken(res, refreshToken);

        // Access Token 이 유효하지만 Refresh Token 은 만료한 경우 → Refresh Token 재발급
        else if (!jwtUtil.hasExpired(accessToken) && jwtUtil.hasExpired(refreshToken))
          processForInDateAccessTokenAndExpiredRefreshToken(res, accessToken, refreshToken);

        // Access Token 과 Refresh Token 모두가 만료된 경우 → 재로그인 요청
        else {
          throw new JoyeusePlaneteApplicationException(ErrorCode.TOKEN_INVALID_REQUEST_LOGIN_EXCEPTION);
        }
      }

    } catch (JoyeusePlaneteApplicationException e) {
      LogUtil.exception("JwtFilter.doFilterInternal (JoyeusePlaneteApplicationException)", e);
      throw e;

    } catch (Exception e) {
      LogUtil.exception("JwtFilter.doFilterInternal (Exception)", e);
      throw new JoyeusePlaneteApplicationException(ErrorCode.UNKNOWN_EXCEPTION, e);
    }

    filterChain.doFilter(req, res);
  }


  // Access Token 과 Refresh Token 모두가 유효한 경우 → 정상 처리
  private void processForValidToken(String accessToken, String refreshToken) {
    Long memberId = jwtUtil.getMemberId(accessToken);
    MemberRole role = jwtUtil.getMemberRole(accessToken);

    Authentication authentication = new UsernamePasswordAuthenticationToken(memberId, null, List.of(new SimpleGrantedAuthority(role.toString())));
    saveAuthentication(authentication);
  }

  private void processForExpiredAccessTokenAndInDateRefreshToken(HttpServletResponse res, String refreshToken) {
    // RefreshToken DB와 비교후 존재할 경우 AccessToken 발행
    if (refreshTokenRepository.existsByToken(refreshToken)) {
      Long memberId = jwtUtil.getMemberId(refreshToken);
      MemberRole role = jwtUtil.getMemberRole(refreshToken);

      String newAccessToken = jwtUtil.generateAccessToken(Payload.generate(memberId, role));
      setAccessTokenToResponse(res, newAccessToken);

      Authentication authentication = new UsernamePasswordAuthenticationToken(memberId, null, List.of(new SimpleGrantedAuthority(role.toString())));
      saveAuthentication(authentication);
    }

    // 아닐 경우에 예외처리
    else {
      throw new JoyeusePlaneteApplicationException(ErrorCode.TOKEN_INVALID_EXCEPTION);
    }
  }

  // Access Token 이 유효하지만 Refresh Token 은 만료한 경우 → Refresh Token 재발급
  private void processForInDateAccessTokenAndExpiredRefreshToken(HttpServletResponse res, String accessToken, String refreshToken) {
    Long memberId = jwtUtil.getMemberId(accessToken);
    MemberRole role = jwtUtil.getMemberRole(accessToken);

    String newRefreshToken = jwtUtil.generateRefreshToken(Payload.generate(memberId, role));
    setRefreshTokenToResponse(res, newRefreshToken);

    refreshTokenRepository.delete(RefreshToken.from(refreshToken, memberId));
    refreshTokenRepository.save(RefreshToken.from(newRefreshToken, memberId));

    Authentication authentication = new UsernamePasswordAuthenticationToken(memberId, null, List.of(new SimpleGrantedAuthority(role.toString())));
    saveAuthentication(authentication);
  }


  private String extractAccessToken(HttpServletRequest request) {
    String token = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (StringUtils.hasText(token) && token.startsWith(GENERAL_TOKEN_PREFIX)) {
      return token.substring(GENERAL_TOKEN_PREFIX.length());
    }

    return null;
  }

  private void setAccessTokenToResponse(HttpServletResponse res, String accessToken) {
    res.setHeader(HttpHeaders.AUTHORIZATION, GENERAL_TOKEN_PREFIX + accessToken);
  }

  private void setRefreshTokenToResponse(HttpServletResponse res, String refreshToken) {
    cookieUtil.setRefreshTokenAsCookie(res, refreshToken);
  }

  private void saveAuthentication(Authentication authentication) {
    SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    SecurityContext context = securityContextHolderStrategy.createEmptyContext();
    context.setAuthentication(authentication);
    securityContextHolderStrategy.setContext(context);
  }
}
