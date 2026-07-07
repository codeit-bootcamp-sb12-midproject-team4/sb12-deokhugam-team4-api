package com.codeit.deokhugam.domain.user;

import lombok.Builder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.codeit.deokhugam.domain.common.SoftDeletableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE `users` SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
//@SQLRestriction("deleted_at IS NULL")
public class User extends SoftDeletableEntity {

	@Column(name = "email", nullable = false, unique = true, length = 320)
	private String email;

	@Column(name = "nickname", nullable = false, length = 20)
	private String nickname;

	@Column(name = "password", nullable = false)
	private String password;

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

}