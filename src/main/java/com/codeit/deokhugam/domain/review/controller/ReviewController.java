package com.codeit.deokhugam.domain.review.controller;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.review.dto.LikedReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewResponse;
import com.codeit.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewUpdateRequest;
import com.codeit.deokhugam.domain.review.service.ReviewService;
import com.codeit.deokhugam.domain.reviewlike.dto.ReviewLikeResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping
	public ResponseEntity<ReviewResponse> createReview(
		@RequestPart @Valid ReviewCreateRequest request,
		@RequestPart(required = false) MultipartFile image
	) {
		ReviewResponse response = reviewService.save(request, image);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{reviewId}")
	public ResponseEntity<ReviewResponse> getReview(
		@PathVariable UUID reviewId,
		@RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
	) {
		ReviewResponse response = reviewService.findByReviewId(reviewId, requestUserId);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<CursorPageResponse<ReviewResponse>> getReviews(
		@RequestParam(required = false) UUID userId,
		@RequestParam(required = false) UUID bookId,
		@RequestParam(required = false) String keyword,
		@RequestParam(defaultValue = "createdAt") String orderBy,
		@RequestParam(defaultValue = "DESC") String direction,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false) Instant after,
		@RequestParam(defaultValue = "50") int limit,
		@RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
	) {
		ReviewSearchRequest request = ReviewSearchRequest.builder()
			.userId(userId)
			.bookId(bookId)
			.keyword(keyword)
			.orderBy(orderBy)
			.direction(direction)
			.cursor(cursor)
			.after(after)
			.limit(limit)
			.requestUserId(requestUserId)
			.build();

		return ResponseEntity.ok(reviewService.findByRequest(request));
	}

	@PatchMapping("/{reviewId}")
	public ResponseEntity<ReviewResponse> updateReview(
		@PathVariable UUID reviewId,
		@RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId,
		@RequestPart @Valid ReviewUpdateRequest request,
		@RequestPart(required = false) MultipartFile image
	) {
		ReviewResponse response = reviewService.update(reviewId, requestUserId, request, image);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{reviewId}")
	public ResponseEntity<Void> softDeleteReview(
		@PathVariable UUID reviewId,
		@RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
	) {
		reviewService.deleteReview(reviewId, requestUserId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{reviewId}/hard")
	public ResponseEntity<Void> hardDeleteReview(
		@PathVariable UUID reviewId,
		@RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
	) {
		reviewService.hardDeleteReview(reviewId, requestUserId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{reviewId}/like")
	public ResponseEntity<ReviewLikeResponse> createLike(
		@PathVariable UUID reviewId,
		@RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
	) {
		ReviewLikeResponse response = reviewService.toggleLike(reviewId, requestUserId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/like/{userId}")
	public ResponseEntity<CursorPageResponse<ReviewResponse>> getLikedReviews(
		@PathVariable UUID userId,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false) Instant after,
		@RequestParam(defaultValue = "50") int limit,
		@RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId
	) {
		LikedReviewSearchRequest request = LikedReviewSearchRequest.builder()
			.userId(userId)
			.cursor(cursor)
			.after(after)
			.limit(limit)
			.build();

		return ResponseEntity.ok(reviewService.findLikedReviews(request, requestUserId));
	}
}
