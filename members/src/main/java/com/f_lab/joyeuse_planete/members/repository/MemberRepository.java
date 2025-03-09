package com.f_lab.joyeuse_planete.members.repository;

import com.f_lab.joyeuse_planete.core.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findMemberByEmail(@Param("email") String email);
}
