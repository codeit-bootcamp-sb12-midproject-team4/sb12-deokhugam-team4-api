package com.codeit.deokhugam.domain.common;

import java.time.Instant;

import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class UpdatableEntity extends BaseEntity {

	@LastModifiedDate
	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

}
