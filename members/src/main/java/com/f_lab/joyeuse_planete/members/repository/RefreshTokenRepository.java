package com.f_lab.joyeuse_planete.members.repository;

import com.f_lab.joyeuse_planete.core.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
  boolean existsByToken(@Param("token") String token);
  void deleteByMemberId(Long memberId);
}
