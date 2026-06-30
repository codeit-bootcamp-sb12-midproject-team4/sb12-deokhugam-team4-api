package com.codeit.deokhugam.domain.review.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.book.BookRepository;
import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.review.dto.LikedReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewResponse;
import com.codeit.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewUpdateRequest;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.review.mapper.ReviewMapper;
import com.codeit.deokhugam.domain.review.repository.ReviewRepository;
import com.codeit.deokhugam.domain.review.service.ReviewService;
import com.codeit.deokhugam.domain.reviewlike.dto.ReviewLikeResponse;
import com.codeit.deokhugam.domain.reviewlike.entity.ReviewLike;
import com.codeit.deokhugam.domain.reviewlike.repository.ReviewLikeRepository;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewMapper reviewMapper;

	private final BookRepository bookRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public ReviewResponse save(ReviewCreateRequest request) {
		Book book = bookRepository.findById(request.getBookId())
			.orElseThrow(() -> new NoSuchElementException("도서를 찾을 수 없습니다."));

		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

		if (reviewRepository.existsByBookIdAndUserId(request.getBookId(), request.getUserId())) {
			throw new NoSuchElementException("해당 도서에 대한 리뷰가 이미 존재합니다.");
		}

		Review review = reviewMapper.toEntity(request, book, user);
		reviewRepository.save(review);

		return reviewMapper.toResponse(review, false);
	}

	@Override
	@Transactional(readOnly = true)
	public ReviewResponse findByReviewId(UUID reviewId, UUID userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다."));

		boolean likedByMe = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId).isPresent();

		return reviewMapper.toResponse(review, likedByMe);
	}

	@Override
	@Transactional(readOnly = true)
	public CursorPageResponse<ReviewResponse> findByRequest(ReviewSearchRequest request) {
		CursorPageResponse<Review> reviewPage = reviewRepository.findReviewsByRequest(request);

		List<UUID> reviewIds = reviewPage.getContent().stream()
			.map(Review::getId)
			.toList();

		Set<UUID> likedReviewIds = new HashSet<>();
		if (request.getRequestUserId() != null && !reviewIds.isEmpty()) {
			likedReviewIds = reviewLikeRepository
				.findReviewIdsByReviewIdInAndUserId(reviewIds, request.getRequestUserId());
		}

		Set<UUID> finalLikedReviewIds = likedReviewIds;
		List<ReviewResponse> content = reviewPage.getContent().stream()
			.map(r -> reviewMapper.toResponse(r, finalLikedReviewIds.contains(r.getId())))
			.toList();

		return CursorPageResponse.<ReviewResponse>builder()
			.content(content)
			.nextCursor(reviewPage.getNextCursor())
			.nextAfter(reviewPage.getNextAfter())
			.size(reviewPage.getSize())
			.totalElements(reviewPage.getTotalElements())
			.hasNext(reviewPage.isHasNext())
			.build();
	}

	@Override
	@Transactional
	public ReviewResponse update(UUID reviewId, UUID userId, ReviewUpdateRequest request) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다."));

		if (!review.isOwnedBy(userId)) {
			throw new IllegalStateException("리뷰 수정 권한이 없습니다.");
		}

		review.update(
			request.getContent(),
			request.getRating()
		);

		boolean likedByMe = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId).isPresent();

		return reviewMapper.toResponse(review, likedByMe);
	}

	@Override
	@Transactional
	public void deleteReview(UUID reviewId, UUID userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다."));

		if (!review.isOwnedBy(userId)) {
			throw new IllegalStateException("리뷰 삭제 권한이 없습니다.");
		}

		reviewRepository.delete(review);
	}

	@Override
	@Transactional
	public void hardDeleteReview(UUID reviewId, UUID userId) {
		Review review = reviewRepository.findByIdIncludingDeleted(reviewId)
			.orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다."));

		if (!review.isOwnedBy(userId)) {
			throw new IllegalStateException("리뷰 삭제 권한이 없습니다.");
		}

		reviewLikeRepository.deleteAllByReviewId(reviewId);
		reviewRepository.hardDeleteById(reviewId);
	}

	@Override
	@Transactional
	public ReviewLikeResponse toggleLike(UUID reviewId, UUID userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new NoSuchElementException("리뷰를 찾을 수 없습니다."));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

		Optional<ReviewLike> existing = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId);

		boolean liked;
		if (existing.isPresent()) {
			reviewLikeRepository.delete(existing.get());
			review.decreaseLikeCount();
			liked = false;
		} else {
			ReviewLike reviewLike = ReviewLike.builder()
				.review(review)
				.user(user)
				.build();
			reviewLikeRepository.save(reviewLike);
			review.increaseLikeCount();
			liked = true;
		}

		return ReviewLikeResponse.builder()
			.reviewId(reviewId)
			.userId(userId)
			.liked(liked)
			.build();
	}

	@Override
	public CursorPageResponse<ReviewResponse> findLikedReviews(LikedReviewSearchRequest request,
		UUID userId) {
		if (!request.getUserId().equals(userId)) {
			throw new IllegalStateException("본인의 좋아요 목록만 조회할 수 있습니다.");
		}

		CursorPageResponse<Review> reviewPage =
			reviewRepository.findLikedReviewsByRequest(request);

		List<ReviewResponse> content = reviewPage.getContent().stream()
			.map(r -> reviewMapper.toResponse(r, true))
			.toList();

		return CursorPageResponse.<ReviewResponse>builder()
			.content(content)
			.nextCursor(reviewPage.getNextCursor())
			.nextAfter(reviewPage.getNextAfter())
			.size(reviewPage.getSize())
			.totalElements(reviewPage.getTotalElements())
			.hasNext(reviewPage.isHasNext())
			.build();
	}
}
