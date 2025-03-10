package com.f_lab.joyeuse_planete.members.controller;


import com.f_lab.joyeuse_planete.core.util.web.ResultResponse;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonResponses;
import com.f_lab.joyeuse_planete.members.request.CreateMemberRequest;
import com.f_lab.joyeuse_planete.members.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

  private final MemberService memberService;


  @PostMapping
  public ResponseEntity<ResultResponse> signup(CreateMemberRequest request) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ResultResponse.of(CommonResponses.CREATE_SUCCESS, HttpStatus.CREATED.value()));
  }
}
