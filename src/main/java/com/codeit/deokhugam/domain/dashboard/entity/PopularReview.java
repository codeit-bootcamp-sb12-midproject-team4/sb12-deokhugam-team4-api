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

@Entity
@Table(
	name = "popular_review",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_popular_review_date", columnNames = {"period", "review_id", "batch_date"}),
		@UniqueConstraint(name = "uq_popular_review_ranking", columnNames = {"period", "batch_date", "ranking"})
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopularReview extends BaseEntity {

	@JdbcTypeCode(SqlTypes.BINARY)
	@Column(name = "review_id", nullable = false)
	private UUID reviewId;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "period", nullable = false, columnDefinition = "ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'ALL_TIME')")
	private PeriodType period;

	@Min(1)
	@Max(50)
	@Column(name = "ranking", nullable = false)
	private Integer ranking;

	@NotBlank
	@Column(name = "book_title", nullable = false, length = 255)
	private String bookTitle;

	@NotBlank
	@Column(name = "book_author", nullable = false, length = 50)
	private String bookAuthor;

	@Column(name = "thumbnail_url", length = 300)
	private String thumbnailUrl;

	@NotBlank
	@Column(name = "user_nickname", nullable = false, length = 20)
	private String userNickname;

	@NotBlank
	@Column(name = "review_content", nullable = false, length = 1000)
	private String reviewContent;

	@Min(0)
	@Max(5)
	@Column(name = "review_rating", nullable = false)
	private Integer reviewRating;

	@DecimalMin("0.00")
	@Column(name = "score", nullable = false, precision = 10, scale = 2)
	private BigDecimal score;

	@PositiveOrZero
	@Column(name = "like_count", nullable = false)
	private Integer likeCount;

	@PositiveOrZero
	@Column(name = "comment_count", nullable = false)
	private Integer commentCount;

	@NotNull
	@Column(name = "batch_date", nullable = false)
	private LocalDate batchDate;
}
