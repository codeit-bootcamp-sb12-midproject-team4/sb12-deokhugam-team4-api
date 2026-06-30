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
public class LikedReviewSearchRequest {
	private UUID userId;
	private String cursor;
	private Instant after;
	private int limit;
	private UUID requestUserId;
}
