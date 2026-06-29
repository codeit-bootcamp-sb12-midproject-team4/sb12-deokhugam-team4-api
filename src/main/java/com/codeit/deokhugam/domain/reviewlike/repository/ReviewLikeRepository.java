package com.codeit.deokhugam.domain.reviewlike.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.codeit.deokhugam.domain.reviewlike.entity.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, UUID> {

	@Query("""
			select r.review.id
			from ReviewLike r
			where r.review.id in :reviewIds
			and r.user.id = :userId
		""")
	Set<UUID> findReviewIdsByReviewIdInAndUserId(List<UUID> reviewIds, UUID userId);

	// 좋아요 존재 여부
	Optional<ReviewLike> findByReviewIdAndUserId(UUID reviewId, UUID userId);

	// likedByMe 체크 (리뷰 조회 시)
	boolean existsByReviewIdAndUserId(UUID reviewId, UUID userId);

	// 물리 삭제 시 연관 좋아요 삭제
	void deleteAllByReviewId(UUID reviewId);
}
