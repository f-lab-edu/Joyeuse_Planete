package com.f_lab.joyeuse_planete.core.util.jwt;


import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.util.jwt.JwtUtil.Payload;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class JwtUtilTest {

  JwtUtil jwtUtil;
  String TEST_SECRET_KEY = "TESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTESTTEST";

  @Mock
  Jwts Jwts;


  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil(new ObjectMapper());
    ReflectionTestUtils.setField(jwtUtil, "JWT_PRIVATE_SECRET_KEY", TEST_SECRET_KEY);
  }


  @DisplayName("access token 생성 성공")
  @Test
  void testGenerateAccessTokenSuccess() {
    // given
    Long memberId = 1L;
    Payload payload = Payload.generate(memberId);

    // when
    String accessToken = jwtUtil.generateAccessToken(payload);
    Long extractedMemberId = jwtUtil.getMemberId(accessToken);
    boolean hasExpired = jwtUtil.hasExpired(accessToken);

    // then
    assertThat(accessToken).matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");
    assertThat(extractedMemberId).isEqualTo(memberId);
    assertThat(hasExpired).isFalse();
  }

  @DisplayName("refresh token 생성 성공")
  @Test
  void testGenerateRefreshTokenSuccess() {
    // given
    Long memberId = 1L;
    Payload payload = Payload.generate(memberId);

    // when
    String refreshToken = jwtUtil.generateRefreshToken(payload);
    Long extractedMemberId = jwtUtil.getMemberId(refreshToken);
    boolean hasExpired = jwtUtil.hasExpired(refreshToken);

    // then
    assertThat(refreshToken).matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");
    assertThat(extractedMemberId).isEqualTo(memberId);
    assertThat(hasExpired).isFalse();
  }

  @DisplayName("encrypt 실패")
  @Test
  void testGenerateTokenFail1() {
    // given
    Long memberId = 1L;
    Payload payload = Payload.generate(memberId);

    try (MockedStatic<Jwts> mockedJwts = mockStatic(Jwts.class)) {
      JwtBuilder mockBuilder = mock(JwtBuilder.class);
      mockedJwts.when(io.jsonwebtoken.Jwts::builder).thenReturn(mockBuilder);
      when(mockBuilder.issuer(anyString())).thenReturn(mockBuilder);
      when(mockBuilder.expiration(any(Date.class))).thenReturn(mockBuilder);
      when(mockBuilder.issuedAt(any(Date.class))).thenReturn(mockBuilder);
      when(mockBuilder.claim(anyString(), any())).thenReturn(mockBuilder);
      when(mockBuilder.signWith(any(SecretKey.class))).thenReturn(mockBuilder);
      when(mockBuilder.compact()).thenThrow(new RuntimeException("JWT generation failed"));

      // when / then
      assertThatThrownBy(() -> jwtUtil.generateAccessToken(payload))
          .isInstanceOf(JoyeusePlaneteApplicationException.class)
          .hasMessage(ErrorCode.TOKEN_ENCRYPTION_FAIL_EXCEPTION.getDescription());
    }
  }
}