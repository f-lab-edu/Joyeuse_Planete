package com.f_lab.joyeuse_planete.members.controller;

import com.f_lab.joyeuse_planete.core.domain.Member;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.util.web.BeanValidationErrorMessage;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonErrorResponses;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonResponses;
import com.f_lab.joyeuse_planete.members.dto.request.MemberUpdateRequestDTO;
import com.f_lab.joyeuse_planete.members.dto.request.SigninRequestDTO;
import com.f_lab.joyeuse_planete.members.dto.request.SignupRequestDTO;
import com.f_lab.joyeuse_planete.members.dto.response.GetMemberResponseDTO;
import com.f_lab.joyeuse_planete.members.dto.response.SigninResponseDTO;
import com.f_lab.joyeuse_planete.members.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = { MemberController.class })
class MemberControllerTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  MemberService memberService;

  static final String MEMBERS_URL_PREFIX = "/api/v1/members";

  @DisplayName("회원 조회 성공")
  @Test
  void testGetMemberSuccess() throws Exception {
    // given
    Long memberId = 1L;
    String nickname = "testnickname";
    String email = "test";
    GetMemberResponseDTO response = createGetMemberResponseDTO(nickname, email);

    // when
    when(memberService.getMember(memberId)).thenReturn(response);

    // then
    mockMvc.perform(get(MEMBERS_URL_PREFIX + "/{memberId}", memberId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(CommonResponses.OK))
        .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()));
  }

  @DisplayName("회원 가입 성공")
  @Test
  void testSignupSuccess() throws Exception {
    // given
    String expectedMsg = CommonResponses.CREATE_SUCCESS;
    int expectedStatusCode = HttpStatus.CREATED.value();

    String nickname = "testnick";
    String email = "test@email.com";
    String password = "Test123!@";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("로그인 성공")
  @Test
  void testSigninSuccess() throws Exception {
    // given
    String expectedMsg = CommonResponses.OK;
    int expectedStatusCode = HttpStatus.OK.value();

    String email = "test@email.com";
    String password = "Test123!@";
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";

    byte[] request = objectMapper.writeValueAsString(createSigninRequestDTO(email, password)).getBytes();
    SigninResponseDTO response = createSigninResponseDTO(accessToken, refreshToken);

    // when
    when(memberService.signin(any())).thenReturn(response);

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX + "/signin")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode))
        .andExpect(jsonPath("$.token.access_token").value(accessToken))
        .andExpect(jsonPath("$.token.refresh_token").value(refreshToken));
  }

  @DisplayName("회원 업데이트 성공")
  @Test
  void testPostMemberUpdateSuccess() throws Exception {
    // given
    Long memberId = 1L;
    String expectedMsg = CommonResponses.UPDATE_SUCCESS;
    int expectedStatusCode = HttpStatus.OK.value();

    String nickname = "testnick";
    String email = "test@email.com";
    String password = "Test123!@";

    byte[] request = objectMapper.writeValueAsString(createMemberUpdateRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).updateMember(any(), any());

    // then
    mockMvc.perform(put(MEMBERS_URL_PREFIX + "/{memberId}", memberId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 삭제 성공")
  @Test
  void testDeleteMemberSuccess() throws Exception {
    // given
    Long memberId = 1L;

    // when
    doNothing().when(memberService).deleteMember(any());

    // then
    mockMvc.perform(delete(MEMBERS_URL_PREFIX + "/{memberId}", memberId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value(CommonResponses.DELETE_SUCCESS))
        .andExpect(jsonPath("$.status_code").value(HttpStatus.OK.value()));
  }

  @DisplayName("존재하지 않는 URL 로 요청시 에러메시지 반환")
  @Test
  void testNonValidURLAndSuccess() throws Exception {
    String INVALID_PATH = "/path/NOT_EXIST_PATH";

    mockMvc.perform(get(INVALID_PATH))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(CommonErrorResponses.INCORRECT_ADDRESS))
        .andExpect(jsonPath("$.status_code").value(HttpStatus.NOT_FOUND.value()));
  }

  @DisplayName("회원 가입 실패1 (비밀번호 대문자 없음)")
  @Test
  void testPostMemberCreateInvalidPasswordFail1() throws Exception {
    // given
    String expectedMsg = BeanValidationErrorMessage.PASSWORD_INVALID_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "testnick";
    String email = "test@email.com";
    String password = "est123!@";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 가입 실패2 (비밀번호 소문자 없음)")
  @Test
  void testPostMemberCreateInvalidPasswordFail2() throws Exception {
    // given
    String expectedMsg = BeanValidationErrorMessage.PASSWORD_INVALID_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "testnick";
    String email = "test@email.com";
    String password = "TEST123!@";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 가입 실패3 (비밀번호 특수문자 없음)")
  @Test
  void testPostMemberCreateInvalidPasswordFail3() throws Exception {
    // given
    String expectedMsg = BeanValidationErrorMessage.PASSWORD_INVALID_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "testnick";
    String email = "test@email.com";
    String password = "TEST123";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 가입 실패4 (비밀번호 길이 8 미만)")
  @Test
  void testPostMemberCreateInvalidPasswordFail4() throws Exception {
    // given
    String expectedMsg = BeanValidationErrorMessage.PASSWORD_INVALID_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "testnick";
    String email = "test@email.com";
    String password = "Test1!";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 가입 실패5 (비밀번호 길이 20 초과)")
  @Test
  void testPostMemberCreateInvalidPasswordFail5() throws Exception {
    // given
    String expectedMsg = BeanValidationErrorMessage.PASSWORD_INVALID_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "testnick";
    String email = "test@email.com";
    String password = "Test1234adsflasdf!#@!@adsfasdfasdfas";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 가입 실패6 (비밀번호 빈 값)")
  @Test
  void testPostMemberCreateInvalidPasswordFail6() throws Exception {
    // given
    String expectedMsg1 = BeanValidationErrorMessage.PASSWORD_NOT_BLANK_PATTERN;
    String expectedMsg2 = BeanValidationErrorMessage.PASSWORD_INVALID_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "testnick";
    String email = "test@email.com";
    String password = "";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(expectedMsg1)))
        .andExpect(content().string(containsString(expectedMsg2)))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 가입 실패7 (닉네임 길이 5 미만)")
  @Test
  void testPostMemberCreateInvalidPasswordFail7() throws Exception {
    // given
    String expectedMsg = BeanValidationErrorMessage.NICKNAME_INVALID_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "tt";
    String email = "test@email.com";
    String password = "Test12#@!@ads";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 가입 실패8 (닉네임 길이 9 초과)")
  @Test
  void testPostMemberCreateInvalidPasswordFail8() throws Exception {
    // given
    String expectedMsg = BeanValidationErrorMessage.NICKNAME_INVALID_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "tta2348934asdfasdf";
    String email = "test@email.com";
    String password = "Test12#@!@ads";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 가입 실패9 (닉네임 빈 값)")
  @Test
  void testPostMemberCreateInvalidPasswordFail9() throws Exception {
    // given
    String expectedMsg1 = BeanValidationErrorMessage.NICKNAME_NOT_BLANK_PATTERN;
    String expectedMsg2 = BeanValidationErrorMessage.NICKNAME_INVALID_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "";
    String email = "test@email.com";
    String password = "Test12#@!@ads";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString(expectedMsg1)))
        .andExpect(content().string(containsString(expectedMsg2)))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 가입 실패10 (닉네임 숫자로 시작)")
  @Test
  void testPostMemberCreateInvalidPasswordFail10() throws Exception {
    // given
    String expectedMsg = BeanValidationErrorMessage.NICKNAME_INVALID_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "1test";
    String email = "test@email.com";
    String password = "Test12#@!@ads";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 가입 실패11 (닉네임 특수문자 사용)")
  @Test
  void testPostMemberCreateInvalidPasswordFail11() throws Exception {
    // given
    String expectedMsg = BeanValidationErrorMessage.NICKNAME_INVALID_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "test###";
    String email = "test@email.com";
    String password = "Test12#@!@ads";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 가입 실패12 (이메일 패턴)")
  @Test
  void testPostMemberCreateInvalidPasswordFail12() throws Exception {
    // given
    String expectedMsg = BeanValidationErrorMessage.EMAIL_INVALID_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "testasd1";
    String email = "testemail.com";
    String password = "Test12#@!@ads";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("회원 가입 실패13 (이메일 빈 값)")
  @Test
  void testPostMemberCreateInvalidPasswordFail13() throws Exception {
    // given
    String expectedMsg = BeanValidationErrorMessage.EMAIL_NOT_BLANK_PATTERN;
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    String nickname = "testasd1";
    String email = "";
    String password = "Test12#@!@ads";

    byte[] request = objectMapper.writeValueAsString(createMemberRequestDTO(nickname, email, password)).getBytes();

    // when
    doNothing().when(memberService).signup(any());

    // then
    mockMvc.perform(post(MEMBERS_URL_PREFIX)
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  @DisplayName("어플리케이션 예외")
  @Test
  void testMemberNotExistExceptionFail() throws Exception {
    // given
    Long memberId = 1L;
    String expectedMsg = ErrorCode.MEMBER_NOT_EXIST_EXCEPTION.getDescription();
    int expectedStatusCode = HttpStatus.BAD_REQUEST.value();

    // when
    when(memberService.getMember(memberId))
        .thenThrow(new JoyeusePlaneteApplicationException(ErrorCode.MEMBER_NOT_EXIST_EXCEPTION));

    // then
    mockMvc.perform(get(MEMBERS_URL_PREFIX + "/{memberId}", memberId))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(expectedMsg))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }

  private SignupRequestDTO createMemberRequestDTO(
      String nickname,
      String email,
      String password
  ) {
    SignupRequestDTO request = new SignupRequestDTO();

    request.setNickname(nickname);
    request.setEmail(email);
    request.setPassword(password);

    return request;
  }

  private MemberUpdateRequestDTO createMemberUpdateRequestDTO(
      String nickname,
      String email,
      String password
  ) {
    MemberUpdateRequestDTO request = new MemberUpdateRequestDTO();

    request.setNickname(nickname);
    request.setEmail(email);
    request.setPassword(password);

    return request;
  }

  private SigninRequestDTO createSigninRequestDTO(
      String email,
      String password
  ) {
    SigninRequestDTO request = new SigninRequestDTO();

    request.setEmail(email);
    request.setPassword(password);

    return request;
  }

  private GetMemberResponseDTO createGetMemberResponseDTO(String nickname, String email) {
    return GetMemberResponseDTO.from(
        Member.builder()
            .email(nickname)
            .nickname(email)
            .build());
  }

  private SigninResponseDTO createSigninResponseDTO(String accessToken, String refreshToken) {
    return SigninResponseDTO.from(accessToken, refreshToken);
  }
}