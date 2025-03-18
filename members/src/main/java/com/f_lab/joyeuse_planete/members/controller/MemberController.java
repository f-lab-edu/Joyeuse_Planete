package com.f_lab.joyeuse_planete.members.controller;

import com.f_lab.joyeuse_planete.core.util.web.ResultResponse;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonResponses;

import com.f_lab.joyeuse_planete.members.dto.request.MemberUpdateRequestDTO;
import com.f_lab.joyeuse_planete.members.dto.request.SigninRequestDTO;
import com.f_lab.joyeuse_planete.members.dto.request.SignupRequestDTO;
import com.f_lab.joyeuse_planete.members.dto.response.SigninResponseDTO;
import com.f_lab.joyeuse_planete.members.service.MemberService;
import com.f_lab.joyeuse_planete.members.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

  private final MemberService memberService;
  private final CookieUtil cookieUtil;

  @PreAuthorize("#memberId == authentication.principal")
  @GetMapping("/{memberId}")
  public ResponseEntity<ResultResponse> getMember(@PathVariable("memberId") Long memberId) {

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(memberService.getMember(memberId));
  }

  @PostMapping
  public ResponseEntity<ResultResponse> signup(
      @RequestBody @Valid SignupRequestDTO request) {

    memberService.signup(request);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ResultResponse.of(CommonResponses.CREATE_SUCCESS, HttpStatus.CREATED.value()));
  }

  @PostMapping("/signin")
  public ResponseEntity<ResultResponse> signin(
      @RequestBody @Valid SigninRequestDTO request, HttpServletResponse res) {

    SigninResponseDTO signinResponseDTO = memberService.signin(request);
    cookieUtil.setRefreshTokenAsCookie(res, signinResponseDTO.getRefreshToken());


    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(signinResponseDTO);
  }


  @PreAuthorize("hasRole('MEMBER') || hasRole('ADMIN')")
  @PostMapping("/signout/{memberId}")
  public ResponseEntity<ResultResponse> signout(Authentication authentication) {

    memberService.signout((Long) authentication.getPrincipal());

    return ResponseEntity
        .ok(ResultResponse.of(CommonResponses.DELETE_SUCCESS, HttpStatus.OK.value()));
  }

  @PreAuthorize("#memberId == authentication.principal")
  @PutMapping("/{memberId}")
  public ResponseEntity<ResultResponse> updateMember(
      @RequestBody @Valid MemberUpdateRequestDTO request,
      @PathVariable("memberId") Long memberId) {

    memberService.updateMember(request, memberId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ResultResponse.of(CommonResponses.UPDATE_SUCCESS, HttpStatus.OK.value()));
  }

  @PreAuthorize("#memberId == authentication.principal")
  @DeleteMapping("/{memberId}")
  public ResponseEntity<ResultResponse> deleteMember(@PathVariable("memberId") Long memberId) {

    memberService.deleteMember(memberId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ResultResponse.of(CommonResponses.DELETE_SUCCESS, HttpStatus.OK.value()));
  }
}
