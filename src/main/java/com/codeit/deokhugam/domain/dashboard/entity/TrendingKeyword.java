package com.codeit.deokhugam.domain.dashboard.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trending_keyword")
@IdClass(TrendingKeywordId.class) // 복합키 클래스 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrendingKeyword {

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "snapshot_id", nullable = false)
	private TrendingKeywordSnapshot snapshot; // 식별자 관계 (PK이자 FK)

	@Id
	@Min(1)
	@Max(10)
	@Column(name = "ranking", nullable = false)
	private Integer ranking;

	@NotBlank
	@Column(name = "keyword", nullable = false, length = 50)
	private String keyword;

	@DecimalMin("0.00")
	@Column(name = "score", nullable = false, precision = 8, scale = 2)
	private BigDecimal score;

	@Builder
	public TrendingKeyword(TrendingKeywordSnapshot snapshot, int ranking, String keyword, BigDecimal score) {
		this.snapshot = snapshot;
		this.ranking = ranking;
		this.keyword = keyword;
		this.score = score;
	}
}
