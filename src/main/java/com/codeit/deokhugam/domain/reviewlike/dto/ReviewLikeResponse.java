package com.codeit.deokhugam.domain.reviewlike.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLikeResponse {
	private UUID reviewId;
	private UUID userId;
	private boolean liked;
}
