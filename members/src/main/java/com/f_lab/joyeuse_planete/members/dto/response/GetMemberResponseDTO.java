package com.f_lab.joyeuse_planete.members.dto.response;

import com.f_lab.joyeuse_planete.core.domain.Member;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class GetMemberResponseDTO extends ResultResponse {

  @JsonProperty("member")
  MemberDTO member;

  private GetMemberResponseDTO(String message, int statusCode, MemberDTO member) {
    super(message, statusCode);
    this.member = member;
  }

  public static GetMemberResponseDTO from(Member member) {
    return new GetMemberResponseDTO(
        CommonResponses.OK,
        HttpStatus.OK.value(),
        MemberDTO.from(member));
  }
}
