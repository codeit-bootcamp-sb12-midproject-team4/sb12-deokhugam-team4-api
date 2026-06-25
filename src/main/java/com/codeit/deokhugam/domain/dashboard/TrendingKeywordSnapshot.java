package com.codeit.deokhugam.domain.dashboard;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "trending_keyword_snapshot",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_trending_keyword_snapshot_time", columnNames = {"calculated_at"})
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrendingKeywordSnapshot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 순차적 PK 생성으로 Clustered Index 최적화
	@Column(name = "snapshot_id", nullable = false)
	private Long snapshotId;

	@NotNull
	@Column(name = "calculated_at", nullable = false)
	private LocalDateTime calculatedAt;

	@Builder
	public TrendingKeywordSnapshot(LocalDateTime calculatedAt) {
		this.calculatedAt = calculatedAt;
	}
}
