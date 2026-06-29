package com.codeit.deokhugam.domain.review.repository;

import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.review.dto.ReviewSearchCondition;
import com.codeit.deokhugam.domain.review.entity.Review;

public interface ReviewRepositoryCustom {

	// 리뷰 조회 목록 (페이지네이션)
	CursorPageResponse<Review> findReviewsByCondition(ReviewSearchCondition condition);
}
