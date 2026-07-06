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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
	name = "popular_book",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_popular_book_dataset_book", columnNames = {"dataset_id","book_id"}),
		@UniqueConstraint(name = "uq_popular_book_dataset_ranking", columnNames = {"dataset_id","ranking"})
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

	@Column(name = "dataset_id", nullable = false)
	private Long datasetId;

	@Enumerated(EnumType.STRING)
	@Column(name = "period", nullable = false)
	private PeriodType period;

	@Column(name = "ranking", nullable = false)
	private Integer ranking;

	@Column(name = "book_title", nullable = false, length = 255)
	private String bookTitle;

	@Column(name = "author", nullable = false, length = 50)
	private String author;

	@Column(name = "thumbnail_url", length = 300)
	private String thumbnailUrl;

	@Column(name = "score", nullable = false, precision = 10, scale = 2)
	private BigDecimal score;

	@Column(name = "review_count", nullable = false)
	private Integer reviewCount;

	@Column(name = "like_count", nullable = false)
	private Integer likeCount;

	@Column(name = "comment_count", nullable = false)
	private Integer commentCount;

	@Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
	private BigDecimal averageRating;

	@Column(name = "batch_date", nullable = false)
	private LocalDate batchDate;
}
