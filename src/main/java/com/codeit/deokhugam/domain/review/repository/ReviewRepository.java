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

	// 리뷰 단건 조회
	Optional<Review> findByIdAndUserId(UUID reviewId, UUID userId);

	@Query(value = "SELECT * FROM Review WHERE id = :id", nativeQuery = true)
	Optional<Review> findByIdIncludingDeleted(@Param("id") UUID id);

	@Modifying
	@Query(value = "DELETE FROM review WHERE id = :id", nativeQuery = true)
	void hardDeleteById(@Param("id") UUID id);
}
