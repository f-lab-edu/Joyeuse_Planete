package com.f_lab.joyeuse_planete.members.repository;

import com.f_lab.joyeuse_planete.core.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
