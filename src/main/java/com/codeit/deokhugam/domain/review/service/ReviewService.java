package com.codeit.deokhugam.domain.review.service;

import java.util.UUID;

import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewResponse;
import com.codeit.deokhugam.domain.review.dto.ReviewSearchCondition;
import com.codeit.deokhugam.domain.review.dto.ReviewUpdateRequest;
import com.codeit.deokhugam.domain.reviewlike.dto.ReviewLikeResponse;

public interface ReviewService {

	ReviewResponse save(ReviewCreateRequest request);

	ReviewResponse findByReviewId(UUID reviewId, UUID userId);

	CursorPageResponse<ReviewResponse> findByCondition(ReviewSearchCondition condition);

	ReviewResponse update(UUID reviewId, UUID userId, ReviewUpdateRequest request);

	void deleteReview(UUID reviewId, UUID userId);

	void hardDeleteReview(UUID reviewId, UUID userId);

	ReviewLikeResponse toggleLike(UUID reviewId, UUID userId);
}
