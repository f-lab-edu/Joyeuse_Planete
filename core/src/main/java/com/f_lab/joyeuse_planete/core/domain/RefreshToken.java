package com.f_lab.joyeuse_planete.core.domain;

import com.f_lab.joyeuse_planete.core.domain.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity implements Persistable<String> {

  @Id
  @Column(nullable = false)
  private String token;

  @Override
  public String getId() {
    return token;
  }

  private Long memberId;

  @Override
  public boolean isNew() {
    return super.getCreatedAt() == null;
  }

  public static RefreshToken from(String token, Long memberId) {
    return new RefreshToken(token, memberId);
  }
}
