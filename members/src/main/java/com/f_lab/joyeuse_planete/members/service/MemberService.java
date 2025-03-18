package com.f_lab.joyeuse_planete.members.service;

import com.f_lab.joyeuse_planete.core.domain.Member;
import com.f_lab.joyeuse_planete.core.domain.RefreshToken;
import com.f_lab.joyeuse_planete.core.domain.repository.RefreshTokenRepository;
import com.f_lab.joyeuse_planete.core.exceptions.ErrorCode;
import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.security.jwt.JwtUtil;
import com.f_lab.joyeuse_planete.core.security.jwt.JwtUtil.Payload;
import com.f_lab.joyeuse_planete.members.dto.request.MemberUpdateRequestDTO;
import com.f_lab.joyeuse_planete.members.dto.request.SigninRequestDTO;
import com.f_lab.joyeuse_planete.members.dto.request.SignupRequestDTO;
import com.f_lab.joyeuse_planete.members.dto.response.GetMemberResponseDTO;
import com.f_lab.joyeuse_planete.members.dto.response.SigninResponseDTO;
import com.f_lab.joyeuse_planete.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtUtil jwtUtil;

  public GetMemberResponseDTO getMember(Long memberId) {
    Member member = findMemberById(memberId);
    return GetMemberResponseDTO.from(member);
  }

  @Transactional
  public SigninResponseDTO signin(SigninRequestDTO request) {
    Member member = findMemberByEmail(request.getEmail());
    validatePassword(member, request.getPassword());

    String accessToken = jwtUtil.generateAccessToken(Payload.generate(member.getId(), member.getRole()));
    String refreshToken = jwtUtil.generateRefreshToken(Payload.generate(member.getId(), member.getRole()));

    refreshTokenRepository.save(RefreshToken.from(refreshToken, member.getId()));

    return SigninResponseDTO.from(accessToken, refreshToken);
  }

  @Transactional
  public void signup(SignupRequestDTO request) {
    validateMemberByEmail(request.getEmail());

    memberRepository.save(
        request.toEntity(passwordEncoder.encode(request.getPassword())));
  }

  @Transactional
  public void signout(Long memberId) {
    refreshTokenRepository.deleteByMemberId(memberId);
  }

  @Transactional
  public void updateMember(MemberUpdateRequestDTO request, Long memberId) {
    Member member = findMemberById(memberId);

    member.updateMember(
        request.getNickname(),
        request.getEmail(),
        passwordEncoder.encode(request.getPassword()));

    memberRepository.save(member);
  }

  @Transactional
  public void deleteMember(Long memberId) {
    Member member = findMemberById(memberId);
    member.setDeleted(true);
    memberRepository.save(member);
  }

  private Member findMemberById(Long memberId) {
    return memberRepository.findById(memberId).orElseThrow(
        () -> new JoyeusePlaneteApplicationException(ErrorCode.MEMBER_NOT_EXIST_EXCEPTION)
    );
  }

  private Member findMemberByEmail(String email) {
    return memberRepository.findMemberByEmail(email).orElseThrow(
        () -> new JoyeusePlaneteApplicationException(ErrorCode.MEMBER_NOT_EXIST_EXCEPTION)
    );
  }

  private void validateMemberByEmail(String email) {
    memberRepository.findMemberByEmail(email).ifPresent(
        member -> {
          throw new JoyeusePlaneteApplicationException(ErrorCode.MEMBER_ALREADY_EXIST_EXCEPTION);
        });
  }

  private void validatePassword(Member member, String password) {
    if (!passwordEncoder.matches(password, member.getPassword()))
      throw new JoyeusePlaneteApplicationException(ErrorCode.MEMBER_PASSWORD_INVALID_EXCEPTION);
  }
}
