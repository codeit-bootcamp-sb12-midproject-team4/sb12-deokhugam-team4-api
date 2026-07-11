package com.codeit.deokhugam.domain.review.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codeit.deokhugam.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

	// 도서별 유저 리뷰 존재 여부
	boolean existsByBookIdAndUserId(UUID bookId, UUID userId);

	@Query("SELECT r FROM Review r JOIN FETCH r.book JOIN FETCH r.user WHERE r.id = :reviewId")
	Optional<Review> findByIdWithDetails(@Param("reviewId") UUID reviewId);

	// 리뷰 단건 조회
	Optional<Review> findByIdAndUserId(UUID reviewId, UUID userId);

	@Query(value = "SELECT * FROM Review WHERE id = :id", nativeQuery = true)
	Optional<Review> findByIdIncludingDeleted(@Param("id") UUID id);

	@Modifying
	@Query(value = "DELETE FROM Review WHERE id = :id", nativeQuery = true)
	void hardDeleteById(@Param("id") UUID id);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Review r SET r.likeCount = r.likeCount + 1 WHERE r.id = :reviewId")
	void increaseLikeCount(@Param("reviewId") UUID reviewId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Review r SET r.likeCount = r.likeCount - 1 WHERE r.id = :reviewId")
	void decreaseLikeCount(@Param("reviewId") UUID reviewId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Review r SET r.commentCount = r.commentCount + 1 WHERE r.id = :reviewId")
	void increaseCommentCount(@Param("reviewId") UUID reviewId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Review r SET r.commentCount = r.commentCount - 1 WHERE r.id = :reviewId")
	void decreaseCommentCount(@Param("reviewId") UUID reviewId);
}
