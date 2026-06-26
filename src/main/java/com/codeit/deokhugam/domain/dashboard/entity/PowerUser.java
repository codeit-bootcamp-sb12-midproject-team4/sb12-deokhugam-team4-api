package com.codeit.deokhugam.domain.dashboard.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.codeit.deokhugam.domain.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
		name = "power_user",
		uniqueConstraints = {
				@UniqueConstraint(name = "uq_power_user_date", columnNames = {"period", "user_id", "batch_date"}),
				@UniqueConstraint(name = "uq_power_user_ranking", columnNames = {"period", "batch_date", "ranking"})
		}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true) // 부모의 id, createdAt까지 로그에 포함
@SuperBuilder // BaseEntity의 @SuperBuilder와 매핑
public class PowerUser extends BaseEntity {

	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "period", nullable = false, columnDefinition = "ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'ALL_TIME')")
	private PeriodType period;

	@NotNull
	@Min(1)
	@Max(10)
	@Column(name = "ranking", nullable = false)
	private Integer ranking;

	@NotBlank
	@Column(name = "nickname", nullable = false, length = 20)
	private String nickname;

	@NotNull
	@DecimalMin("0.00")
	@Column(name = "score", nullable = false, precision = 10, scale = 2)
	private BigDecimal score;

	@NotNull
	@PositiveOrZero
	@Column(name = "like_count", nullable = false)
	private Integer likeCount;

	@NotNull
	@PositiveOrZero
	@Column(name = "comment_count", nullable = false)
	private Integer commentCount;

	@NotNull
	@Column(name = "batch_date", nullable = false)
	private LocalDate batchDate;
}
