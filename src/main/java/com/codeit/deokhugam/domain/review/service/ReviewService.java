package com.codeit.deokhugam.domain.review.service;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.review.dto.LikedReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewResponse;
import com.codeit.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewUpdateRequest;
import com.codeit.deokhugam.domain.reviewlike.dto.ReviewLikeResponse;

public interface ReviewService {

	ReviewResponse save(ReviewCreateRequest request, MultipartFile image);

	ReviewResponse findByReviewId(UUID reviewId, UUID userId);

	CursorPageResponse<ReviewResponse> findByRequest(ReviewSearchRequest request);

	ReviewResponse update(UUID reviewId, UUID userId, ReviewUpdateRequest request, MultipartFile image);

	void deleteReview(UUID reviewId, UUID userId);

	void hardDeleteReview(UUID reviewId, UUID userId);

	ReviewLikeResponse toggleLike(UUID reviewId, UUID userId);

	CursorPageResponse<ReviewResponse> findLikedReviews(LikedReviewSearchRequest request, UUID userId);
}
