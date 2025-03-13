package com.f_lab.joyeuse_planete.members.service;

import com.f_lab.joyeuse_planete.core.domain.Member;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.members.dto.request.MemberUpdateRequestDTO;
import com.f_lab.joyeuse_planete.members.dto.request.SignupRequestDTO;
import com.f_lab.joyeuse_planete.members.dto.response.GetMemberResponseDTO;
import com.f_lab.joyeuse_planete.members.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

  @InjectMocks
  MemberService memberService;

  @Mock
  MemberRepository memberRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  @DisplayName("회원 조회시 성공")
  @Test
  void testGetMemberSuccess() {
    // given
    Long memberId = 1L;
    String nickname = "test";
    String email = "test@test.com";
    String password = "test1234!";

    Member member = createMember(memberId, nickname, email, password);

    // when
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    GetMemberResponseDTO response = memberService.getMember(memberId);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.value());
    assertThat(response.getMember().getNickname()).isEqualTo(nickname);
    assertThat(response.getMember().getEmail()).isEqualTo(email);
  }

  @DisplayName("회원가입시 성공")
  @Test
  void testSignupSuccess() {
    // given
    String nickname = "test";
    String email = "test@test.com";
    String password = "test1234!";

    SignupRequestDTO request = createMemberRequestDTO(nickname, email, password);

    // when
    when(memberRepository.findMemberByEmail(email)).thenReturn(Optional.empty());
    memberService.signup(request);

    // then
    verify(memberRepository, times(1)).save(any(Member.class));
  }

  @DisplayName("회원 업데이트시 성공")
  @Test
  void testUpdateMemberSuccess() {
    // given
    Long memberId = 1L;
    String nickname = "test";
    String email = "test@test.com";
    String password = "test1234!";

    String nickname2 = "test2";
    String email2 = "test@test.com2";
    String password2 = "test1234!2";

    Member member = createMember(memberId, nickname, email, password);
    MemberUpdateRequestDTO request = createMemberUpdateRequestDTO(nickname2, email2, password2);

    // when
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    when(passwordEncoder.encode(password2)).thenReturn(password2);
    memberService.updateMember(request, memberId);

    // then
    assertThat(member.getNickname()).isEqualTo(nickname2);
    assertThat(member.getEmail()).isEqualTo(email2);
    assertThat(member.getPassword()).isEqualTo(password2);
  }

  @DisplayName("회원 삭제시 is_deleted field 를 true 처리")
  @Test
  void testDeleteMemberSuccess() {
    // given
    Long memberId = 1L;
    String nickname = "test";
    String email = "test@test.com";
    String password = "test1234!";

    Member member = createMember(memberId, nickname, email, password);

    // when
    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
    memberService.deleteMember(memberId);

    // then
    assertThat(member.isDeleted()).isTrue();
  }

  @DisplayName("이미 존재하는 회원에 대한 회원가입 은 실패")
  @Test
  void testAlreadyExistMemberOnSignupThenThrowExceptionFail() {
    // given
    Long memberId = 1L;
    String nickname = "test";
    String email = "test@test.com";
    String password = "test1234!";

    Member member = createMember(memberId, nickname, email, password);
    SignupRequestDTO request = createMemberRequestDTO(nickname, email, password);

    // when
    when(memberRepository.findMemberByEmail(email)).thenReturn(Optional.of(member));

    // then
    assertThatThrownBy(() -> memberService.signup(request))
        .isInstanceOf(JoyeusePlaneteApplicationException.class)
        .hasMessage(ErrorCode.MEMBER_ALREADY_EXIST_EXCEPTION.getDescription());
  }

  @DisplayName("존재하지 않는 회원에 대한 operation 은 실패")
  @Test
  void testNotExistMemberThenThrowException() {
    // given
    Long memberId = 1L;

    // when
    when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

    // then
    assertThatThrownBy(() -> memberService.deleteMember(memberId))
        .isInstanceOf(JoyeusePlaneteApplicationException.class)
        .hasMessage(ErrorCode.MEMBER_NOT_EXIST_EXCEPTION.getDescription());
  }

  private Member createMember(
      Long id,
      String nickname,
      String email,
      String password
  ) {
    return Member.builder()
        .id(id)
        .nickname(nickname)
        .email(email)
        .password(password)
        .build();
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
}