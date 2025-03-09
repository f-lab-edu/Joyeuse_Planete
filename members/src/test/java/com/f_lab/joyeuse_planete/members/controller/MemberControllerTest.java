package com.f_lab.joyeuse_planete.members.controller;

import com.f_lab.joyeuse_planete.core.util.web.ResultResponse.CommonResponses;
import com.f_lab.joyeuse_planete.members.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = { MemberController.class })
class MemberControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  MemberService memberService;

  static final String MEMBER_URL_PREFIX = "/api/v1/members";

  @DisplayName("회원 생성시 성공")
  @Test
  void testCreateMemberSuccess() throws Exception {
    // given
    String expectedMessage = CommonResponses.CREATE_SUCCESS;
    int expectedStatusCode = HttpStatus.CREATED.value();

    // when
//    when(memberService);

    // then
    mockMvc.perform(MockMvcRequestBuilders.post(MEMBER_URL_PREFIX))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.message").value(expectedMessage))
        .andExpect(jsonPath("$.status_code").value(expectedStatusCode));
  }
}