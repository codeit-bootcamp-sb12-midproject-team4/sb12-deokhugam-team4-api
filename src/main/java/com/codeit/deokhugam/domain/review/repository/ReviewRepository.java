package com.codeit.deokhugam.domain.review.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeit.deokhugam.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {

	// 도서별 유저 리뷰 존재 여부
	boolean existsByBookIdAndUserId(UUID bookId, UUID userId);

	// 리뷰 단건 조회
	Optional<Review> findByIdAndUserId(UUID reviewId, UUID userId);
}
