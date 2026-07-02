package com.codeit.deokhugam.domain.dashboard.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;

@Entity
@Table(
		name = "trending_keyword_snapshot",
		uniqueConstraints = {
				@UniqueConstraint(name = "uq_trending_keyword_snapshot_time", columnNames = {"calculated_at"})
		}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더 패턴의 컴파일 안정성을 위해 추가
public class TrendingKeywordSnapshot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="dataset_id", nullable = false)
	private Long datasetId;

	@Column(name = "calculated_at", nullable = false)
	private LocalDateTime calculatedAt;
}
