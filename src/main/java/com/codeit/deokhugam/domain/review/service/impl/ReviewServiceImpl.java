package com.codeit.deokhugam.domain.review.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.book.BookRepository;
import com.codeit.deokhugam.domain.client.s3.FileStorageClient;
import com.codeit.deokhugam.domain.client.s3.ImgType;
import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.notification.event.ReviewLikedEvent;
import com.codeit.deokhugam.domain.review.dto.LikedReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewResponse;
import com.codeit.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewUpdateRequest;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.review.exception.ReviewAlreadyExistsException;
import com.codeit.deokhugam.domain.review.exception.ReviewNotFoundException;
import com.codeit.deokhugam.domain.review.exception.ReviewNotOwnedException;
import com.codeit.deokhugam.domain.review.mapper.ReviewMapper;
import com.codeit.deokhugam.domain.review.repository.ReviewRepository;
import com.codeit.deokhugam.domain.review.service.ReviewService;
import com.codeit.deokhugam.domain.reviewlike.dto.ReviewLikeResponse;
import com.codeit.deokhugam.domain.reviewlike.entity.ReviewLike;
import com.codeit.deokhugam.domain.reviewlike.repository.ReviewLikeRepository;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewMapper reviewMapper;

	private final BookRepository bookRepository;
	private final UserRepository userRepository;

	private final ApplicationEventPublisher eventPublisher;

	private final FileStorageClient fileStorageClient;

	@Override
	@Transactional
	public ReviewResponse save(ReviewCreateRequest request, MultipartFile image) {
		Book book = bookRepository.findById(request.getBookId())
			.orElseThrow(() -> new NoSuchElementException("도서를 찾을 수 없습니다."));

		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

		if (reviewRepository.existsByBookIdAndUserId(request.getBookId(), request.getUserId())) {
			log.info("오류 : 이미 작성된 리뷰가 있습니다");
			throw ReviewAlreadyExistsException.withBookAndUser(request.getBookId(), request.getUserId());
		}

		String imgKey = null;
		if (image != null && !image.isEmpty()) {
			imgKey = fileStorageClient.uploadImage(image, ImgType.PREFIX_REVIEW);
		}

		Review review = reviewMapper.toEntity(request, book, user);
		review.updateAttachmentUrl(imgKey);
		reviewRepository.save(review);

		bookRepository.increaseRatingAndCountBulk(book.getId(), request.getRating());

		return toResponseWithUrls(review, false);
	}

	@Override
	@Transactional(readOnly = true)
	public ReviewResponse findByReviewId(UUID reviewId, UUID userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> ReviewNotFoundException.withReviewId(reviewId));

		boolean likedByMe = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId).isPresent();

		return toResponseWithUrls(review, likedByMe);
	}

	@Override
	@Transactional(readOnly = true)
	public CursorPageResponse<ReviewResponse> findByRequest(ReviewSearchRequest request) {
		CursorPageResponse<Review> reviewPage = reviewRepository.findReviewsByRequest(request);

		log.info("조회된 리뷰결과	: {}", reviewPage.getContent().size());

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
			.map(r -> toResponseWithUrls(r, finalLikedReviewIds.contains(r.getId())))
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
	public ReviewResponse update(UUID reviewId, UUID userId, ReviewUpdateRequest request, MultipartFile image) {
		Review review = reviewRepository.findByIdWithDetails(reviewId)
			.orElseThrow(() -> ReviewNotFoundException.withReviewId(reviewId));

		if (!review.isOwnedBy(userId)) {
			throw ReviewNotOwnedException.withUserId(userId);
		}

		if (image != null && !image.isEmpty()) {
			if (review.getAttachmentUrl() != null) {
				fileStorageClient.deleteImage(review.getAttachmentUrl());
			}
			String imgKey = fileStorageClient.uploadImage(image, ImgType.PREFIX_REVIEW);
			review.updateAttachmentUrl(imgKey);
		}

		int oldRating = review.getRating();
		review.update(request.getContent(), request.getRating());
		int newRating = review.getRating();
		if (oldRating != newRating) {
			bookRepository.updateRatingBulk(review.getBook().getId(), oldRating, newRating);
		}
		boolean likedByMe = reviewLikeRepository.findByReviewIdAndUserId(reviewId, userId).isPresent();

		return toResponseWithUrls(review, likedByMe);
	}

	@Override
	@Transactional
	public void deleteReview(UUID reviewId, UUID userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> ReviewNotFoundException.withReviewId(reviewId));

		if (!review.isOwnedBy(userId)) {
			throw ReviewNotOwnedException.withUserId(userId);
		}

		reviewRepository.delete(review);

		bookRepository.decreaseRatingAndCountBulk(review.getBook().getId(), review.getRating());
	}

	@Override
	@Transactional
	public void hardDeleteReview(UUID reviewId, UUID userId) {
		Review review = reviewRepository.findByIdIncludingDeleted(reviewId)
			.orElseThrow(() -> ReviewNotFoundException.withReviewId(reviewId));

		if (!review.isOwnedBy(userId)) {
			throw ReviewNotOwnedException.withUserId(userId);
		}

		reviewLikeRepository.deleteAllByReviewId(reviewId);
		reviewRepository.hardDeleteById(reviewId);
	}

	@Override
	@Transactional
	public ReviewLikeResponse toggleLike(UUID reviewId, UUID userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> ReviewNotFoundException.withReviewId(reviewId));

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

			eventPublisher.publishEvent(new ReviewLikedEvent(
				reviewId,
				userId
			));
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
			throw ReviewNotOwnedException.withUserId(userId);
		}

		CursorPageResponse<Review> reviewPage =
			reviewRepository.findLikedReviewsByRequest(request);

		List<ReviewResponse> content = reviewPage.getContent().stream()
			.map(r -> toResponseWithUrls(r, true))
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

	private ReviewResponse toResponseWithUrls(Review review, boolean likedByMe) {
		ReviewResponse response = reviewMapper.toResponse(review, likedByMe);

		if (review.getAttachmentUrl() != null) {
			response.setAttachmentUrl(fileStorageClient.getAttachFileUrl(review.getAttachmentUrl()));
		}

		if (review.getBook().getThumbnailKey() != null) {
			response.setBookThumbnailUrl(fileStorageClient.getAttachFileUrl(review.getBook().getThumbnailKey()));
		}

		return response;
	}
}
