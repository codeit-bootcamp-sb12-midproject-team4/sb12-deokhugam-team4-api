package com.codeit.deokhugam.domain.review.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSearchCondition {
	private UUID userId;
	private UUID bookId;
	private String keyword;
	private String orderBy;
	private String direction;
	private String cursor;
	private Instant after;
	private int limit;
	private UUID requestUserId;
}
