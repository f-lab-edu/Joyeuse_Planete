package com.f_lab.joyeuse_planete.core.security.filter;

import com.f_lab.joyeuse_planete.core.exceptions.JoyeusePlaneteApplicationException;
import com.f_lab.joyeuse_planete.core.util.web.ResultResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JwtExceptionFilter extends OncePerRequestFilter {


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (JoyeusePlaneteApplicationException e) {
      sendApplicationErrorResponse(response, e);
    } catch (Exception e) {
      sendErrorResponse(response, e);
    }
  }

  private void sendErrorResponse(
      HttpServletResponse response, Exception e) throws IOException {

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    response.getWriter().write(
        new ObjectMapper().writeValueAsString(
            ResultResponse.of(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value())
        )
    );
  }

  private void sendApplicationErrorResponse(
      HttpServletResponse response, JoyeusePlaneteApplicationException e) throws IOException {

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    response.getWriter().write(
        new ObjectMapper().writeValueAsString(
            ResultResponse.of(
                e.getErrorCode().getDescription(),
                e.getErrorCode().getStatus())
        )
    );
  }
}
