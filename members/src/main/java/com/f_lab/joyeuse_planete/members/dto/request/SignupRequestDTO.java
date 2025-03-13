package com.f_lab.joyeuse_planete.members.dto.request;

import com.f_lab.joyeuse_planete.core.domain.Member;
import com.f_lab.joyeuse_planete.core.domain.MemberRole;
import com.f_lab.joyeuse_planete.core.util.web.BeanValidationErrorMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupRequestDTO {

  @NotBlank(message = BeanValidationErrorMessage.NICKNAME_NOT_BLANK_PATTERN)
  @Pattern(regexp = "^[a-zA-Z][0-9a-zA-Z]{4,9}$", message = BeanValidationErrorMessage.NICKNAME_INVALID_PATTERN)
  private String nickname;

  @NotBlank(message = BeanValidationErrorMessage.EMAIL_NOT_BLANK_PATTERN)
  @Email(message = BeanValidationErrorMessage.EMAIL_INVALID_PATTERN)
  private String email;

  @NotBlank(message = BeanValidationErrorMessage.PASSWORD_NOT_BLANK_PATTERN)
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])[A-Za-z0-9@#$%^&+=!?]{8,20}$", message = BeanValidationErrorMessage.PASSWORD_INVALID_PATTERN)
  private String password;

  public Member toEntity() {
    return Member.builder()
        .nickname(nickname)
        .email(email)
        .password(password)
        .role(MemberRole.MEMBER)
        .build();
  }

  public Member toEntity(String encodedPassword) {
    return Member.builder()
        .nickname(nickname)
        .email(email)
        .password(encodedPassword)
        .role(MemberRole.MEMBER)
        .build();
  }
}
