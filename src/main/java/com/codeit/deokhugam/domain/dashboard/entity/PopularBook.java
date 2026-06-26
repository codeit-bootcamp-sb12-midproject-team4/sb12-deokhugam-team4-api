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
import jakarta.validation.constraints.DecimalMax;
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
		name = "popular_book",
		uniqueConstraints = {
				@UniqueConstraint(name = "uq_popular_book_date", columnNames = {"period", "book_id", "batch_date"}),
				@UniqueConstraint(name = "uq_popular_book_ranking", columnNames = {"period", "batch_date", "ranking"})
		}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true) // 부모의 id, createdAt까지 한눈에 로그로 찍히도록 설정
@SuperBuilder // 부모의 @SuperBuilder와 완벽하게 한 쌍을 이룸
public class PopularBook extends BaseEntity {

	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "book_id", nullable = false)
	private UUID bookId;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "period", nullable = false, columnDefinition = "ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'ALL_TIME')")
	private PeriodType period;

	@NotNull
	@Min(1)
	@Max(50)
	@Column(name = "ranking", nullable = false)
	private Integer ranking;

	@NotBlank
	@Column(name = "book_title", nullable = false, length = 255)
	private String bookTitle;

	@NotBlank
	@Column(name = "author", nullable = false, length = 50)
	private String author;

	@Column(name = "thumbnail_url", length = 300)
	private String thumbnailUrl;

	@NotNull
	@DecimalMin("0.00")
	@Column(name = "score", nullable = false, precision = 10, scale = 2)
	private BigDecimal score;

	@NotNull
	@PositiveOrZero
	@Column(name = "review_count", nullable = false)
	private Integer reviewCount;

	@NotNull
	@PositiveOrZero
	@Column(name = "like_count", nullable = false)
	private Integer likeCount;

	@NotNull
	@PositiveOrZero
	@Column(name = "comment_count", nullable = false)
	private Integer commentCount;

	@NotNull
	@DecimalMin("0.00")
	@DecimalMax("5.00")
	@Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
	private BigDecimal averageRating;

	@NotNull
	@Column(name = "batch_date", nullable = false)
	private LocalDate batchDate;
}
