package com.codeit.deokhugam.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.codeit.deokhugam.domain.common.SoftDeletableEntity;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE `user` SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
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