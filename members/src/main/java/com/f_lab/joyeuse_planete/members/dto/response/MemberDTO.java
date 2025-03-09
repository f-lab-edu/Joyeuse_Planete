package com.f_lab.joyeuse_planete.members.dto.response;

import com.f_lab.joyeuse_planete.core.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

  private String nickname;

  private String email;

  public static MemberDTO from(Member member) {
    return new MemberDTO(member.getNickname(), member.getEmail());
  }
}
