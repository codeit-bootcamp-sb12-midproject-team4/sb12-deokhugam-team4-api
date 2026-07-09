package com.codeit.deokhugam.domain.review.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.review.dto.LikedReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.reviewlike.entity.ReviewLike;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.global.config.QueryDslConfig;

@DataJpaTest(properties = {
	"spring.sql.init.mode=never",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@Import(QueryDslConfig.class)
class ReviewRepositoryTest {

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private TestEntityManager entityManager;

	private User user1;
	private User user2;
	private User user3;

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
		user3 = entityManager.persist(new User(
			"user3@email.com",
			"용가리치킨",
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
			.build()
		);
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
			.thumbnailKey("url")
			.reviewCount(0L)
			.rating(0.0)
			.build());
	}

	@Test
	@DisplayName("Review 엔티티 - update 메서드")
	void review_uppdate() {
		Review review = Review.builder()
			.content("원본 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();

		review.update("수정된 리뷰", 3);

		assertThat(review.getContent()).isEqualTo("수정된 리뷰");
		assertThat(review.getRating()).isEqualTo(3);
	}

	@Test
	@DisplayName("Review 엔티티 - isOwnedBy 메서드")
	void review_isOwnedBy() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();

		assertThat(review.isOwnedBy(user1.getId())).isTrue();
		assertThat(review.isOwnedBy(user2.getId())).isFalse();
	}

	@Test
	@DisplayName("리뷰 단건 조회 성공")
	void findByIdAndUserId_success() {
		Review review = entityManager.persist(Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build());

		var foundReview = reviewRepository.findByIdAndUserId(review.getId(), user1.getId());

		assertThat(foundReview).isPresent();
		assertThat(foundReview.get().getId()).isEqualTo(review.getId());
	}

	@Test
	@DisplayName("리뷰 단건 조회 실패 - 논리 삭제된 리뷰 조회")
	void findById_softDelete_fail() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		// 논리 삭제
		reviewRepository.delete(review);

		Optional<Review> result = reviewRepository.findById(review.getId());
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("리뷰 단건 조회 성공 - 논리 삭제된 리뷰 조회(findByIdIncludingDeleted)")
	void findByIdIncludingDeleted_success() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		// 논리 삭제
		reviewRepository.delete(review);

		// findByIdIncludingDeleted: 논리 삭제 포함 조회
		Optional<Review> result = reviewRepository.findByIdIncludingDeleted(review.getId());
		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(review.getId());
		assertThat(result.get().getDeletedAt()).isNotNull();
	}

	@Test
	@DisplayName("리뷰 존재 여부 확인 - 이미 리뷰가 존재하는 경우")
	void existsByBookIdAndUserId_true() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review);

		boolean exists = reviewRepository.existsByBookIdAndUserId(book1.getId(), user1.getId());

		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("리뷰 존재 여부 확인 - 리뷰가 없는 경우")
	void existsByBookIdAndUserId_false() {
		boolean exists = reviewRepository.existsByBookIdAndUserId(book1.getId(), user2.getId());

		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("리뷰 물리 삭제 - 삭제 후 조회 X")
	void hardDeleteById_success() {
		Review review = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		Review savedReview = reviewRepository.save(review);

		reviewRepository.hardDeleteById(savedReview.getId());
		entityManager.flush();
		entityManager.clear();

		Optional<Review> result = reviewRepository.findById(savedReview.getId());
		assertThat(result).isEmpty();

		Optional<Review> resultIncludingDeleted = reviewRepository.findByIdIncludingDeleted(savedReview.getId());
		assertThat(resultIncludingDeleted).isEmpty();
	}

	@Test
	@DisplayName("리뷰 목록 조회 성공")
	void findReviewsByRequest_success() {
		Review review1 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);

		Review review2 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review2);

		Review review3 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user3)
			.build();
		reviewRepository.save(review3);

		// limit가 전체 데이터보다 큰 경우 (hasNext = false)
		ReviewSearchRequest request1 = ReviewSearchRequest.builder()
			.limit(10)
			.build();

		CursorPageResponse<Review> result1 = reviewRepository.findReviewsByRequest(request1);

		assertThat(result1.getContent()).hasSize(3);
		assertThat(result1.getTotalElements()).isEqualTo(3L);
		assertThat(result1.isHasNext()).isFalse();

		// limit가 전체 데이터보다 작은 경우 (hasNext = true)
		ReviewSearchRequest request2 = ReviewSearchRequest.builder()
			.limit(2)
			.build();

		CursorPageResponse<Review> result2 = reviewRepository.findReviewsByRequest(request2);

		assertThat(result2.getContent()).hasSize(2);
		assertThat(result2.getTotalElements()).isEqualTo(3L);
		assertThat(result2.isHasNext()).isTrue();
	}

	@Test
	@DisplayName("리뷰 목록 조회 - userId 필터링")
	void findReviewsByRequest_userIdFilter() {
		Review review1 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);

		Review review2 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book2)
			.user(user1)
			.build();
		reviewRepository.save(review2);

		Review review3 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review3);

		entityManager.flush();
		entityManager.clear();

		// user1이 작성한 리뷰 조회: review1, review2
		ReviewSearchRequest request = ReviewSearchRequest.builder()
			.userId(user1.getId())
			.build();

		CursorPageResponse<Review> result = reviewRepository.findReviewsByRequest(request);

		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent()).allMatch(r -> r.getUser().getId().equals(user1.getId()));
		assertThat(result.getTotalElements()).isEqualTo(2L);
	}

	@Test
	@DisplayName("리뷰 목록 조회 - bookId 필터링")
	void findReviewsByRequest_bookIdFilter() {
		Review review1 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);

		Review review2 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review2);

		Review review3 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book2)
			.user(user3)
			.build();
		reviewRepository.save(review3);

		entityManager.flush();
		entityManager.clear();

		// book1의 리뷰 검색: review1, review2
		ReviewSearchRequest request = ReviewSearchRequest.builder()
			.bookId(book1.getId())
			.build();

		CursorPageResponse<Review> result = reviewRepository.findReviewsByRequest(request);

		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent()).allMatch(r -> r.getBook().getId().equals(book1.getId()));
		assertThat(result.getTotalElements()).isEqualTo(2L);
	}

	@Test
	@DisplayName("리뷰 목록 조회 - content로 keyword 검색")
	void findReviewsByRequest_contentKeywordSearch() {
		Review review1 = Review.builder()
			.content("완전 최고")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);

		Review review2 = Review.builder()
			.content("그냥 그래요")
			.rating(3)
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review2);

		Review review3 = Review.builder()
			.content("완전 별루")
			.rating(1)
			.book(book1)
			.user(user3)
			.build();
		reviewRepository.save(review3);

		// 내용에 '완전'이 들어가는 리뷰 조회
		ReviewSearchRequest request = ReviewSearchRequest.builder()
			.keyword("완전")
			.build();

		CursorPageResponse<Review> result = reviewRepository.findReviewsByRequest(request);

		// review1("완전 최고"), review2("완전 별루")
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(2L);

		assertThat(result.getContent()).allMatch(r -> r.getContent().contains("완전"));
	}

	@Test
	@DisplayName("리뷰 목록 조회 - nickname으로 keyword 검색")
	void findReviewsByRequest_nicknameKeywordSearch() {
		Review review1 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);

		Review review2 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review2);

		Review review3 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user3)
			.build();
		reviewRepository.save(review3);

		// nickname에 '용'이 들어가는 리뷰 작성자 검색
		ReviewSearchRequest request = ReviewSearchRequest.builder()
			.keyword("용")
			.build();

		CursorPageResponse<Review> result = reviewRepository.findReviewsByRequest(request);

		// user1("졸려용"), user3("용가리치킨")
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(2L);

		assertThat(result.getContent())
			.extracting(review -> review.getUser().getNickname())
			.containsExactlyInAnyOrder("졸려용", "용가리치킨");
	}

	@Test
	@DisplayName("리뷰 목록 조회 - bookTitle로 keyword 검색")
	void findReviewsByRequest_bookTitleKeywordSearch() {
		Review review1 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);

		Review review2 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book2)
			.user(user1)
			.build();
		reviewRepository.save(review2);

		Review review3 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book3)
			.user(user1)
			.build();
		reviewRepository.save(review3);

		// 도서 제목에 '왕자'가 들어가는 리뷰 조회
		ReviewSearchRequest request = ReviewSearchRequest.builder()
			.keyword("왕자")
			.build();

		CursorPageResponse<Review> result = reviewRepository.findReviewsByRequest(request);

		// book1("어린 왕자"), book2("개구리 왕자")
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(2L);

		assertThat(result.getContent())
			.extracting(review -> review.getBook().getTitle())
			.containsExactlyInAnyOrder("어린 왕자", "개구리 왕자");
	}

	@Test
	@DisplayName("리뷰 목록 조회 - rating DESC 정렬")
	void findReviewsByRequest_ratingDesc() {
		Review review1 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);

		Review review2 = Review.builder()
			.content("테스트 리뷰")
			.rating(3)
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review2);

		Review review3 = Review.builder()
			.content("테스트 리뷰")
			.rating(1)
			.book(book1)
			.user(user3)
			.build();
		reviewRepository.save(review3);

		// rating 기준 DESC 정렬
		ReviewSearchRequest request = ReviewSearchRequest.builder()
			.orderBy("rating")
			.direction("DESC")
			.build();

		CursorPageResponse<Review> result = reviewRepository.findReviewsByRequest(request);

		assertThat(result.getContent()).hasSize(3);

		// 평점 5 -> 3 -> 1 순
		assertThat(result.getContent().get(0).getRating()).isEqualTo(5);
		assertThat(result.getContent().get(1).getRating()).isEqualTo(3);
		assertThat(result.getContent().get(2).getRating()).isEqualTo(1);
	}

	@Test
	@DisplayName("리뷰 목록 조회 - rating ASC 정렬")
	void findReviewsByRequest_ratingAsc() {
		Review review1 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);

		Review review2 = Review.builder()
			.content("테스트 리뷰")
			.rating(3)
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review2);

		Review review3 = Review.builder()
			.content("테스트 리뷰")
			.rating(1)
			.book(book1)
			.user(user3)
			.build();
		reviewRepository.save(review3);

		// rating 기준 DESC 정렬
		ReviewSearchRequest request = ReviewSearchRequest.builder()
			.orderBy("rating")
			.direction("ASC")
			.build();

		CursorPageResponse<Review> result = reviewRepository.findReviewsByRequest(request);

		assertThat(result.getContent()).hasSize(3);

		// 평점 1 -> 3 -> 5 순
		assertThat(result.getContent().get(0).getRating()).isEqualTo(1);
		assertThat(result.getContent().get(1).getRating()).isEqualTo(3);
		assertThat(result.getContent().get(2).getRating()).isEqualTo(5);
	}

	@Test
	@DisplayName("리뷰 목록 조회 - createdAt DESC 정렬")
	void findReviewsByRequest_createdAtDesc() throws InterruptedException {
		Review review1 = Review.builder()
			.content("리뷰1")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);
		Thread.sleep(10);

		Review review2 = Review.builder()
			.content("리뷰2")
			.rating(5)
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review2);
		Thread.sleep(10);

		Review review3 = Review.builder()
			.content("리뷰3")
			.rating(5)
			.book(book1)
			.user(user3)
			.build();
		reviewRepository.save(review3);

		ReviewSearchRequest request = ReviewSearchRequest.builder()
			.orderBy("createdAt")
			.direction("DESC")
			.build();

		CursorPageResponse<Review> result = reviewRepository.findReviewsByRequest(request);

		// 리뷰3 -> 리뷰2 -> 리뷰1 순
		assertThat(result.getContent()).hasSize(3);
		assertThat(result.getContent().get(0).getContent()).isEqualTo("리뷰3");
		assertThat(result.getContent().get(1).getContent()).isEqualTo("리뷰2");
		assertThat(result.getContent().get(2).getContent()).isEqualTo("리뷰1");
	}

	@Test
	@DisplayName("리뷰 목록 조회 - createdAt ASC 정렬")
	void findReviewsByRequest_createdAtAsc() throws InterruptedException {
		Review review1 = Review.builder()
			.content("리뷰1")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);
		Thread.sleep(10);

		Review review2 = Review.builder()
			.content("리뷰2")
			.rating(5)
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review2);
		Thread.sleep(10);

		Review review3 = Review.builder()
			.content("리뷰3")
			.rating(5)
			.book(book1)
			.user(user3)
			.build();
		reviewRepository.save(review3);

		ReviewSearchRequest request = ReviewSearchRequest.builder()
			.orderBy("createdAt")
			.direction("ASC")
			.build();

		CursorPageResponse<Review> result = reviewRepository.findReviewsByRequest(request);

		assertThat(result.getContent()).hasSize(3);

		// 리뷰1 -> 리뷰2 -> 리뷰3 순
		assertThat(result.getContent().get(0).getContent()).isEqualTo("리뷰1");
		assertThat(result.getContent().get(1).getContent()).isEqualTo("리뷰2");
		assertThat(result.getContent().get(2).getContent()).isEqualTo("리뷰3");
	}

	@Test
	@DisplayName("리뷰 목록 조회 - 커서 페이지네이션 (rating ASC 정렬)")
	void findReviewsByRequest_cursorPaging_ratingAsc() throws InterruptedException {
		Review review1 = Review.builder()
			.content("테스트 리뷰1")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);
		Thread.sleep(10);

		Review review2 = Review.builder()
			.content("테스트 리뷰2")
			.rating(3)
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review2);
		Thread.sleep(10);

		Review review3 = Review.builder()
			.content("테스트 리뷰3")
			.rating(1)
			.book(book1)
			.user(user3)
			.build();
		reviewRepository.save(review3);

		// limit가 1일 때 첫 번째 페이지 조회
		ReviewSearchRequest request1 = ReviewSearchRequest.builder()
			.orderBy("rating")
			.direction("ASC")
			.limit(1)
			.build();

		entityManager.flush();
		entityManager.clear();

		CursorPageResponse<Review> page1 = reviewRepository.findReviewsByRequest(request1);
		assertThat(page1.getContent()).hasSize(1);
		assertThat(page1.isHasNext()).isTrue();

		// limit가 1일 때 두 번째 페이지 조회
		ReviewSearchRequest request2 = ReviewSearchRequest.builder()
			.orderBy("rating")
			.direction("ASC")
			.limit(1)
			.cursor(page1.getNextCursor())
			.after(page1.getNextAfter())
			.build();

		CursorPageResponse<Review> page2 = reviewRepository.findReviewsByRequest(request2);

		assertThat(page2.getContent()).hasSize(1);
		assertThat(page2.getContent().get(0).getContent()).isNotEqualTo(page1.getContent().get(0).getContent());
	}

	@Test
	@DisplayName("리뷰 목록 조회 - 커서 페이지네이션 (createdAt DESC 정렬)")
	void findReviewsByRequest_cursorPaging_createdAt() throws InterruptedException {
		Review review1 = Review.builder()
			.content("테스트 리뷰1")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);
		Thread.sleep(10);

		Review review2 = Review.builder()
			.content("테스트 리뷰2")
			.rating(5)
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review2);
		Thread.sleep(10);

		Review review3 = Review.builder()
			.content("테스트 리뷰3")
			.rating(5)
			.book(book1)
			.user(user3)
			.build();
		reviewRepository.save(review3);

		entityManager.flush();
		entityManager.clear();

		// limit가 1일 때 첫 번째 페이지 조회
		ReviewSearchRequest request1 = ReviewSearchRequest.builder()
			.orderBy("createdAt")
			.direction("DESC")
			.limit(2)
			.build();

		CursorPageResponse<Review> page1 = reviewRepository.findReviewsByRequest(request1);

		assertThat(page1.getContent()).hasSize(2);
		assertThat(page1.getContent().get(0).getContent()).isEqualTo("테스트 리뷰3");
		assertThat(page1.isHasNext()).isTrue();

		// limit가 1일 때 두 번째 페이지 조회
		ReviewSearchRequest request2 = ReviewSearchRequest.builder()
			.orderBy("createdAt")
			.direction("DESC")
			.limit(2)
			.cursor(page1.getNextCursor())
			.after(page1.getNextAfter())
			.build();

		CursorPageResponse<Review> page2 = reviewRepository.findReviewsByRequest(request2);

		assertThat(page2.getContent()).hasSize(1);
		assertThat(page2.getContent().get(0).getContent()).isEqualTo("테스트 리뷰1");
		assertThat(page2.isHasNext()).isFalse();
	}

	@Test
	@DisplayName("리뷰 목록 조회 - 논리 삭제된 리뷰 제외 확인")
	void findReviewsByRequest_softDelete() {
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
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review2);

		// 리뷰1 논리 삭제
		reviewRepository.delete(review1);

		entityManager.flush();
		entityManager.clear();

		ReviewSearchRequest request = ReviewSearchRequest.builder().limit(10).build();

		CursorPageResponse<Review> result = reviewRepository.findReviewsByRequest(request);

		// 리뷰1 제외
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getContent()).isEqualTo("테스트 리뷰2");
		assertThat(result.getTotalElements()).isEqualTo(1L);
	}

	@Test
	@DisplayName("리뷰 목록 조회 - hasNext true/false")
	void findReviewsByRequest_hasNextCheck() {
		Review review1 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build();
		reviewRepository.save(review1);

		Review review2 = Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user2)
			.build();
		reviewRepository.save(review2);

		Review review3 = Review.builder()
			.content("테스트 리뷰3")
			.rating(5)
			.book(book1)
			.user(user3)
			.build();
		reviewRepository.save(review3);

		entityManager.flush();
		entityManager.clear();

		// hasNext가 false인 경우
		ReviewSearchRequest request1 = ReviewSearchRequest.builder().limit(5).build();
		CursorPageResponse<Review> result1 = reviewRepository.findReviewsByRequest(request1);
		assertThat(result1.isHasNext()).isFalse();

		// hasNext가 true인 경우
		ReviewSearchRequest request2 = ReviewSearchRequest.builder().limit(2).build();
		CursorPageResponse<Review> result2 = reviewRepository.findReviewsByRequest(request2);
		assertThat(result2.isHasNext()).isTrue();
		assertThat(result2.getContent()).hasSize(2);
	}

	@Test
	@DisplayName("리뷰 목록 조회 - totalElements")
	void findReviewsByRequest_totalElementsAccuracy() {
		Review review1 = entityManager.persist(Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user1)
			.build());
		reviewRepository.save(review1);

		Review review2 = entityManager.persist(Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book2)
			.user(user1)
			.build());
		reviewRepository.save(review2);

		Review review3 = entityManager.persist(Review.builder()
			.content("테스트 리뷰")
			.rating(5)
			.book(book1)
			.user(user2)
			.build());
		reviewRepository.save(review3);

		entityManager.flush();
		entityManager.clear();

		// 전체 조회 시 totalElements
		ReviewSearchRequest requestAll = ReviewSearchRequest.builder().limit(1).build();
		CursorPageResponse<Review> resultAll = reviewRepository.findReviewsByRequest(requestAll);
		assertThat(resultAll.getTotalElements()).isEqualTo(3L);

		// 특정 userId 필터링 시 totalElements
		ReviewSearchRequest requestUser = ReviewSearchRequest.builder()
			.userId(user1.getId())
			.limit(1)
			.build();
		CursorPageResponse<Review> resultUser = reviewRepository.findReviewsByRequest(requestUser);
		assertThat(resultUser.getTotalElements()).isEqualTo(2L);
	}

	@Test
	@DisplayName("좋아요한 리뷰 목록 조회 성공")
	void findLikedReviewsByRequest_success() {
		Review review = entityManager.persist(Review.builder()
			.content("좋아요 대상 리뷰")
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

		// user1이 좋아요한 리뷰 조회
		LikedReviewSearchRequest request = LikedReviewSearchRequest.builder()
			.userId(user1.getId())
			.limit(10)
			.build();

		CursorPageResponse<Review> result = reviewRepository.findLikedReviewsByRequest(request);

		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getContent()).isEqualTo("좋아요 대상 리뷰");
		assertThat(result.getTotalElements()).isEqualTo(1L);
	}

	@Test
	@DisplayName("좋아요한 리뷰 목록 조회 - hasNext true/false")
	void findLikedReviewsByRequest_HasNext() {
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

		entityManager.persist(ReviewLike.builder().user(user1).review(review1).build());
		entityManager.persist(ReviewLike.builder().user(user1).review(review2).build());
		entityManager.persist(ReviewLike.builder().user(user1).review(review3).build());

		entityManager.flush();
		entityManager.clear();

		// hasNext true
		LikedReviewSearchRequest request1 = LikedReviewSearchRequest.builder()
			.userId(user1.getId())
			.limit(2)
			.build();

		CursorPageResponse<Review> result1 = reviewRepository.findLikedReviewsByRequest(request1);
		assertThat(result1.isHasNext()).isTrue();
		assertThat(result1.getContent()).hasSize(2);

		// hasNext false
		LikedReviewSearchRequest request2 = LikedReviewSearchRequest.builder()
			.userId(user1.getId())
			.limit(10)
			.build();

		CursorPageResponse<Review> result2 = reviewRepository.findLikedReviewsByRequest(request2);
		assertThat(result2.isHasNext()).isFalse();
		assertThat(result2.getContent()).hasSize(3);
	}
}