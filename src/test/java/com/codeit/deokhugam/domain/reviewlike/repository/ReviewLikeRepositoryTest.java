package com.codeit.deokhugam.domain.reviewlike.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.reviewlike.entity.ReviewLike;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.global.config.QueryDslConfig;

@DataJpaTest(properties = {
	"spring.sql.init.mode=never",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(QueryDslConfig.class)
class ReviewLikeRepositoryTest {

	@Autowired
	private ReviewLikeRepository reviewLikeRepository;

	@Autowired
	private TestEntityManager entityManager;

	private User user1;
	private User user2;
	private Book book1;
	private Book book2;
	private Book book3;

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
			.thumbnailKey("thumbnail-key")
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
			.thumbnailKey("thumbnail-key")
			.reviewCount(0L)
			.rating(0.0)
			.build());
		book3 = entityManager.persist(Book.builder()
			.title("백설공주")
			.author("사과")
			.description("거울과 가위바위보하는 이야기입니다.")
			.publisher("실화")
			.publishedDate(LocalDate.of(2026, 1, 3))
			.isbn("34567")
			.thumbnailKey("thumbnail-key")
			.reviewCount(0L)
			.rating(0.0)
			.build());
	}

	@Test
	@DisplayName("좋아요 조회 성공")
	void findByReviewIdAndUserId_success() {
		Review review = entityManager.persist(Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build());

		entityManager.persist(ReviewLike.builder()
			.user(user1)
			.review(review)
			.build());

		entityManager.flush();
		entityManager.clear();

		Optional<ReviewLike> result = reviewLikeRepository.findByReviewIdAndUserId(
			review.getId(), user1.getId());

		assertThat(result).isPresent();
		assertThat(result.get().getUser().getId()).isEqualTo(user1.getId());
		assertThat(result.get().getReview().getId()).isEqualTo(review.getId());
	}

	@Test
	@DisplayName("좋아요 조회 실패 - 존재하지 않는 좋아요")
	void findByReviewIdAndUserId_notFound() {
		Review review = entityManager.persist(Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build());

		entityManager.flush();
		entityManager.clear();

		Optional<ReviewLike> result = reviewLikeRepository.findByReviewIdAndUserId(
			review.getId(), user1.getId());

		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("내가 좋아요한 리뷰 목록 조회 성공")
	void findReviewIdsByReviewIdInAndUserId_success() {
		Review review1 = entityManager.persist(Review.builder()
			.content("리뷰1")
			.rating(5)
			.book(book1)
			.user(user1)
			.build());

		Review review2 = entityManager.persist(Review.builder()
			.content("리뷰2")
			.rating(5)
			.book(book2)
			.user(user1)
			.build());

		Review review3 = entityManager.persist(Review.builder()
			.content("리뷰3")
			.rating(5)
			.book(book3)
			.user(user1)
			.build());

		entityManager.persist(ReviewLike.builder()
			.user(user1)
			.review(review1)
			.build());

		entityManager.persist(ReviewLike.builder()
			.user(user1)
			.review(review2)
			.build());

		entityManager.flush();
		entityManager.clear();

		Set<UUID> result = reviewLikeRepository.findReviewIdsByReviewIdInAndUserId(
			List.of(review1.getId(), review2.getId(), review3.getId()),
			user1.getId()
		);

		// review1, review2에만 좋아요
		assertThat(result).hasSize(2);
		assertThat(result).contains(review1.getId(), review2.getId());
		assertThat(result).doesNotContain(review3.getId());
	}

	@Test
	@DisplayName("좋아요 존재 여부 - true")
	void existsByReviewIdAndUserId_true() {
		Review review = entityManager.persist(Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build());

		entityManager.persist(ReviewLike.builder()
			.user(user1)
			.review(review)
			.build());

		entityManager.flush();
		entityManager.clear();

		boolean exists = reviewLikeRepository.findByReviewIdAndUserId(
			review.getId(), user1.getId()).isPresent();

		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("좋아요 존재 여부 - false")
	void existsByReviewIdAndUserId_false() {
		Review review = entityManager.persist(Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build());

		entityManager.flush();
		entityManager.clear();

		boolean exists = reviewLikeRepository.findByReviewIdAndUserId(
			review.getId(), user1.getId()).isPresent();

		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("리뷰 ID로 좋아요 전체 삭제")
	void deleteAllByReviewId_success() {
		Review review = entityManager.persist(Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build());

		entityManager.persist(ReviewLike.builder()
			.user(user1)
			.review(review)
			.build());

		entityManager.persist(ReviewLike.builder()
			.user(user2)
			.review(review)
			.build());

		entityManager.flush();
		entityManager.clear();

		reviewLikeRepository.deleteAllByReviewId(review.getId());
		entityManager.flush();
		entityManager.clear();

		Optional<ReviewLike> result1 = reviewLikeRepository.findByReviewIdAndUserId(
			review.getId(), user1.getId());
		Optional<ReviewLike> result2 = reviewLikeRepository.findByReviewIdAndUserId(
			review.getId(), user2.getId());

		assertThat(result1).isEmpty();
		assertThat(result2).isEmpty();
	}
}