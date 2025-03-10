package com.f_lab.joyeuse_planete.members.dto.request;

import com.f_lab.joyeuse_planete.core.util.web.BeanValidationErrorMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SigninRequestDTO {

  @NotBlank(message = BeanValidationErrorMessage.EMAIL_NOT_BLANK_PATTERN)
  @Email(message = BeanValidationErrorMessage.EMAIL_INVALID_PATTERN)
  private String email;

  @NotBlank(message = BeanValidationErrorMessage.PASSWORD_NOT_BLANK_PATTERN)
  @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])[A-Za-z0-9@#$%^&+=!?]{8,20}$", message = BeanValidationErrorMessage.PASSWORD_INVALID_PATTERN)
  private String password;
}
