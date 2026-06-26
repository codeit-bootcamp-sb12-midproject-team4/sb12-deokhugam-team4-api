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
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "trending_keyword")
@IdClass(TrendingKeywordId.class) // 복합키 클래스 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString // 로그 추적을 위해 추가
@Builder // 클래스 레벨로 이동하여 생성자 코드 청소
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrendingKeyword {

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "snapshot_id", nullable = false)
	@ToString.Exclude
	private TrendingKeywordSnapshot snapshot; // 식별자 관계 (PK이자 FK)

	@Id
	@NotNull
	@Min(1)
	@Max(10)
	@Column(name = "ranking", nullable = false)
	private Integer ranking;

	@NotBlank
	@Column(name = "keyword", nullable = false, length = 50)
	private String keyword;

	@NotNull // 래퍼 타입 안정성 확보를 위해 추가
	@DecimalMin("0.00")
	@Column(name = "score", nullable = false, precision = 8, scale = 2)
	private BigDecimal score;
}
