package com.codeit.deokhugam.domain.review.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.review.dto.LikedReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewResponse;
import com.codeit.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewUpdateRequest;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.review.mapper.ReviewMapperImpl;
import com.codeit.deokhugam.domain.review.repository.ReviewRepository;
import com.codeit.deokhugam.domain.review.service.impl.ReviewServiceImpl;
import com.codeit.deokhugam.domain.reviewlike.dto.ReviewLikeResponse;
import com.codeit.deokhugam.domain.reviewlike.repository.ReviewLikeRepository;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.global.config.QueryDslConfig;

@DataJpaTest(properties = {
	"spring.sql.init.mode=never",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import({
	ReviewServiceImpl.class,
	ReviewMapperImpl.class,
	QueryDslConfig.class
})
class ReviewServiceTest {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private ReviewLikeRepository reviewLikeRepository;

	@Autowired
	private TestEntityManager entityManager;

	private User user1;
	private User user2;

	private Book book1;
	private Book book2;

	@BeforeEach
	void setUp() {
		user1 = entityManager.persist(new User(
			"user1@email.com",
			"졸려용",
			"password"
		));
		user2 = entityManager.persist(new User(
			"user2@email.com",
			"바보",
			"password"
		));

		book1 = entityManager.persist(Book.builder()
			.title("어린 왕자")
			.author("보아뱀")
			.description("모자를 찾아 떠나는 내용입니다.")
			.publisher("아니지롱")
			.publishedDate(LocalDate.of(2026, 1, 1))
			.isbn("12345")
			.thumbnailUrl("url")
			.reviewCount(0L)
			.rating(0.0)
			.build());
		book2 = entityManager.persist(Book.builder()
			.title("개구리 왕자")
			.author("개구리")
			.description("케로로 자서전입니다.")
			.publisher("아니지롱")
			.publishedDate(LocalDate.of(2026, 1, 2))
			.isbn("23456")
			.thumbnailUrl("url")
			.reviewCount(0L)
			.rating(0.0)
			.build());
	}

	@Test
	@DisplayName("리뷰 저장 성공")
	void save_success() {
		ReviewCreateRequest request = new ReviewCreateRequest();
		ReflectionTestUtils.setField(request, "bookId", book1.getId());
		ReflectionTestUtils.setField(request, "userId", user1.getId());
		ReflectionTestUtils.setField(request, "content", "테스트 리뷰");
		ReflectionTestUtils.setField(request, "rating", 5);

		ReviewResponse result = reviewService.save(request);

		assertThat(result.getContent()).isEqualTo("테스트 리뷰");
		assertThat(result.getRating()).isEqualTo(5);
		assertThat(result.getBookTitle()).isEqualTo("어린 왕자");
		assertThat(result.getUserNickname()).isEqualTo("졸려용");
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 도서 정보 없음")
	void save_bookNotFound_fail() {
		ReviewCreateRequest request = new ReviewCreateRequest();
		ReflectionTestUtils.setField(request, "bookId", UUID.randomUUID());
		ReflectionTestUtils.setField(request, "userId", user1.getId());
		ReflectionTestUtils.setField(request, "content", "테스트 리뷰");
		ReflectionTestUtils.setField(request, "rating", 5);

		assertThatThrownBy(() -> reviewService.save(request))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 사용자 정보 없음")
	void save_userNotFound_fail() {
		ReviewCreateRequest request = new ReviewCreateRequest();
		ReflectionTestUtils.setField(request, "bookId", book1.getId());
		ReflectionTestUtils.setField(request, "userId", UUID.randomUUID());
		ReflectionTestUtils.setField(request, "content", "테스트 리뷰");
		ReflectionTestUtils.setField(request, "rating", 5);

		assertThatThrownBy(() -> reviewService.save(request))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 중복 리뷰 등록")
	void save_duplicateReview_fail() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		ReviewCreateRequest request = new ReviewCreateRequest();
		org.springframework.test.util.ReflectionTestUtils.setField(request, "bookId", book1.getId());
		org.springframework.test.util.ReflectionTestUtils.setField(request, "userId", user1.getId());
		org.springframework.test.util.ReflectionTestUtils.setField(request, "content", "중복 리뷰");
		org.springframework.test.util.ReflectionTestUtils.setField(request, "rating", 5);

		assertThatThrownBy(() -> reviewService.save(request))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	@DisplayName("리뷰 단건 조회 성공")
	void findByReviewId_success() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		ReviewResponse result = reviewService.findByReviewId(review.getId(), user1.getId());

		assertThat(result.getContent()).isEqualTo("테스트 리뷰");
		assertThat(result.getRating()).isEqualTo(5);
		assertThat(result.isLikedByMe()).isFalse();
	}

	@Test
	@DisplayName("리뷰 단건 조회 실패 - 존재하지 않는 리뷰")
	void findByReviewId_notFound_fail() {
		assertThatThrownBy(() -> reviewService.findByReviewId(UUID.randomUUID(), user1.getId()))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	@DisplayName("리뷰 목록 조회 성공")
	void findByRequest_success() {
		Review review1 = Review.builder()
			.content("테스트 리뷰1")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);

		Review review2 = Review.builder()
			.content("테스트 리뷰2")
			.rating(5)
			.book(book2)
			.user(user2)
			.build();
		reviewRepository.save(review2);

		ReviewSearchRequest request = ReviewSearchRequest.builder()
			.requestUserId(user1.getId())
			.build();

		CursorPageResponse<ReviewResponse> result = reviewService.findByRequest(request);

		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(2L);
	}

	@Test
	@DisplayName("리뷰 수정 성공")
	void update_success() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		ReviewUpdateRequest updateRequest = new ReviewUpdateRequest();
		ReflectionTestUtils.setField(updateRequest, "content", "수정된 리뷰");
		ReflectionTestUtils.setField(updateRequest, "rating", 3);

		ReviewResponse result = reviewService.update(review.getId(), user1.getId(), updateRequest);

		assertThat(result.getContent()).isEqualTo("수정된 리뷰");
		assertThat(result.getRating()).isEqualTo(3);
	}

	@Test
	@DisplayName("리뷰 수정 실패 - 권한 없음")
	void update_unauthorized_fail() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		ReviewUpdateRequest updateRequest = new ReviewUpdateRequest();
		ReflectionTestUtils.setField(updateRequest, "content", "수정된 리뷰");
		ReflectionTestUtils.setField(updateRequest, "rating", 3);

		assertThatThrownBy(() -> reviewService.update(review.getId(), user2.getId(), updateRequest))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	@DisplayName("리뷰 수정 실패 - 존재하지 않는 리뷰")
	void update_notFound_fail() {
		ReviewUpdateRequest updateRequest = new ReviewUpdateRequest();
		org.springframework.test.util.ReflectionTestUtils.setField(updateRequest, "content", "수정된 리뷰");
		org.springframework.test.util.ReflectionTestUtils.setField(updateRequest, "rating", 3);

		assertThatThrownBy(() -> reviewService.update(UUID.randomUUID(), user1.getId(), updateRequest))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	@DisplayName("리뷰 논리 삭제 성공")
	void deleteReview_success() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		reviewService.deleteReview(review.getId(), user1.getId());

		assertThat(reviewRepository.findById(review.getId())).isEmpty();
	}

	@Test
	@DisplayName("리뷰 논리 삭제 실패 - 권한 없음")
	void deleteReview_unauthorized_fail() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		assertThatThrownBy(() -> reviewService.deleteReview(review.getId(), user2.getId()))
			.isInstanceOf(IllegalStateException.class);
	}

	@Test
	@DisplayName("리뷰 물리 삭제 성공")
	void hardDeleteReview_success() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		reviewService.hardDeleteReview(review.getId(), user1.getId());

		assertThat(reviewRepository.findByIdIncludingDeleted(review.getId())).isEmpty();
	}

	@Test
	@DisplayName("리뷰 물리 삭제 실패 - 존재하지 않는 리뷰")
	void hardDeleteReview_notFound_fail() {
		assertThatThrownBy(() -> reviewService.hardDeleteReview(UUID.randomUUID(), user1.getId()))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	@DisplayName("좋아요 추가 성공")
	void toggleLike_add_success() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		ReviewLikeResponse result = reviewService.toggleLike(review.getId(), user2.getId());

		assertThat(result.isLiked()).isTrue();
		assertThat(reviewLikeRepository.findByReviewIdAndUserId(review.getId(), user2.getId())).isPresent();
	}

	@Test
	@DisplayName("좋아요 취소 성공")
	void toggleLike_cancel_success() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		reviewService.toggleLike(review.getId(), user2.getId());

		// 좋아요 취소
		ReviewLikeResponse result = reviewService.toggleLike(review.getId(), user2.getId());

		assertThat(result.isLiked()).isFalse();
		assertThat(reviewLikeRepository.findByReviewIdAndUserId(review.getId(), user2.getId())).isNotPresent();
	}

	@Test
	@DisplayName("좋아요 실패 - 존재하지 않는 리뷰")
	void toggleLike_reviewNotFound_fail() {
		assertThatThrownBy(() -> reviewService.toggleLike(UUID.randomUUID(), user1.getId()))
			.isInstanceOf(NoSuchElementException.class);
	}

	@Test
	@DisplayName("좋아요한 리뷰 목록 조회 성공")
	void findLikedReviews_success() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		reviewService.toggleLike(review.getId(), user2.getId());

		LikedReviewSearchRequest request = LikedReviewSearchRequest.builder()
			.userId(user2.getId())
			.build();

		CursorPageResponse<ReviewResponse> result = reviewService.findLikedReviews(request, user2.getId());

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getContent()).isEqualTo("테스트 리뷰");
	}

	@Test
	@DisplayName("좋아요한 리뷰 목록 조회 실패 - 권한 없음")
	void findLikedReviews_unauthorized_fail() {
		LikedReviewSearchRequest request = LikedReviewSearchRequest.builder()
			.userId(user1.getId())
			.build();

		assertThatThrownBy(() -> reviewService.findLikedReviews(request, user2.getId()))
			.isInstanceOf(IllegalStateException.class);
	}
}