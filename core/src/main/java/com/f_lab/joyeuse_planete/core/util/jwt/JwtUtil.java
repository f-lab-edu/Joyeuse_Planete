package com.f_lab.joyeuse_planete.core.util.jwt;

import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.util.time.TimeConstants.TimeConstantsMillis;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


@Component
@RequiredArgsConstructor
public class JwtUtil {

  private final ObjectMapper objectMapper;

  @Value("${jwt.secret-key:secret}")
  private String JWT_PRIVATE_SECRET_KEY;
  private static final String JWT_TOKEN_ISSUER = "JOYEUSE_PLANETE";
  private static final String JWT_PAYLOAD_IDENTITY_FIELD = "member";
  private static final long JWT_EXPIRATION_DATE_ACCESS = TimeConstantsMillis.THIRTY_MINUTES;
  private static final long JWT_EXPIRATION_DATE_REFRESH = TimeConstantsMillis.ONE_DAY;;


  public String generateAccessToken(Payload payload) {
    return encrypt(payload, JWT_EXPIRATION_DATE_ACCESS);
  }

  public String generateRefreshToken(Payload payload) {
    return encrypt(payload, JWT_EXPIRATION_DATE_REFRESH);
  }

  public Long getMemberId(String token) {
    return objectMapper.convertValue(decrypt(token).getPayload().get(JWT_PAYLOAD_IDENTITY_FIELD), Payload.class).getMemberId();
  }

  public boolean hasExpired(String token) {
    return decrypt(token).getPayload().getExpiration().before(new Date());
  }

  private String encrypt(Payload payload, long expiration) {
    try {
      return Jwts.builder()
          .issuer(JWT_TOKEN_ISSUER)
          .expiration(new Date(System.currentTimeMillis() + expiration))
          .issuedAt(new Date())
          .claim(JWT_PAYLOAD_IDENTITY_FIELD, payload)
          .signWith(generateKey())
          .compact();

    } catch (Exception e) {
      throw new JoyeusePlaneteApplicationException(ErrorCode.TOKEN_ENCRYPTION_FAIL_EXCEPTION, e);
    }
  }

  private Jws<Claims> decrypt(String token) {
    try {
      return Jwts.parser()
          .verifyWith(generateKey())
          .build()
          .parseSignedClaims(token);

    } catch (JwtException e) {
      throw new JoyeusePlaneteApplicationException(ErrorCode.TOKEN_INVALID_EXCEPTION, e);
    }
  }

  private SecretKey generateKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_PRIVATE_SECRET_KEY));
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Payload {

    @JsonProperty("member_id")
    private Long memberId;

    public static Payload generate(Long memberId) {
      return new Payload(memberId);
    }
  }
}
