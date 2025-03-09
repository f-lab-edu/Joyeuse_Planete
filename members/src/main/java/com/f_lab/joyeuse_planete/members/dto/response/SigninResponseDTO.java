package com.f_lab.joyeuse_planete.members.dto.response;

import com.f_lab.joyeuse_planete.core.util.web.ResultResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
public class SigninResponseDTO extends ResultResponse {

  @JsonProperty("token")
  private Token token;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  static class Token {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    public static Token of(String accessToken, String refreshToken) {
      return new Token(accessToken, refreshToken);
    }
  }

  private SigninResponseDTO(String accessToken, String refreshToken) {
    super(CommonResponses.OK, HttpStatus.OK.value());
    this.token = Token.of(accessToken, refreshToken);
  }

  public static SigninResponseDTO from(String accessToken, String refreshToken) {
    return new SigninResponseDTO(accessToken, refreshToken);
  }
}
