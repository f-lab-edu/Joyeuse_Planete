package com.f_lab.joyeuse_planete.core.domain;

import com.f_lab.joyeuse_planete.core.domain.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("is_deleted IS FALSE")
@Table(name = "members")
public class Member extends BaseEntity {

  @Id @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private String nickname;

  private String email;

  private String password;

  @Enumerated(STRING)
  private MemberRole role;

  public void updateMember(
      String nickname,
      String email,
      String password
  ) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;
  }
}
