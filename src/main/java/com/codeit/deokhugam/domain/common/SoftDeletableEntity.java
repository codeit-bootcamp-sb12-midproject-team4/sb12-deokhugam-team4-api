package com.codeit.deokhugam.domain.common;

import java.time.Instant;

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
public abstract class SoftDeletableEntity extends UpdatableEntity {

	// soft delete
	@Column(name = "deleted_at")
	private Instant deletedAt;

	public void markDeleted() {
		this.deletedAt = Instant.now();
	}

	public boolean isDeleted() {
		return this.deletedAt != null;
	}

	public void updateDeleteAt(Instant deletedAt) {
		this.deletedAt = deletedAt;
	}

}
