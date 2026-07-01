package com.codeit.deokhugam.domain.review.repository;

import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.review.dto.ReviewSearchCondition;
import com.codeit.deokhugam.domain.review.dto.LikedReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.codeit.deokhugam.domain.review.entity.Review;

public interface ReviewRepositoryCustom {

	// 리뷰 목록 조회 (페이지네이션)
	CursorPageResponse<Review> findReviewsByRequest(ReviewSearchRequest request);

	// 좋아요 누른 리뷰 목록 조회 (페이지네이션)
	CursorPageResponse<Review> findLikedReviewsByRequest(LikedReviewSearchRequest request);

}
