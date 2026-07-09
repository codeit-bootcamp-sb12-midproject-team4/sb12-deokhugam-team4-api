package com.codeit.deokhugam.domain.book.dto;

import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookSearchRequest {

	@Nullable
	private UUID userId;

	@Nullable
	private String keyword;

	@NotBlank(message = "정렬 기준은 필수입니다.")
	@Pattern(regexp = "title|publishedDate|rating|reviewCount|score", message = "정렬 기준은 title, publishedDate, rating, reviewCount, score 중 하나여야 합니다.")
	private String orderBy;

	@Builder.Default
	@Pattern(regexp = "ASC|DESC", message = "정렬 방향은 ASC 또는 DESC여야 합니다.")
	private String direction = "DESC";

	@Nullable
	private String cursor;

	@Nullable
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private String after;

	@Builder.Default
	@Min(value = 1, message = "최소 1개 이상 요청해야 합니다.")
	@Max(value = 100, message = "최대 100개까지만 조회 가능합니다.")
	private Integer limit = 50;

}
