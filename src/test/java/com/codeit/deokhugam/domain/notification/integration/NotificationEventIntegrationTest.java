package com.codeit.deokhugam.domain.notification.integration;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;
import com.codeit.deokhugam.domain.notification.entity.Notification;
import com.codeit.deokhugam.domain.notification.event.CommentCreatedEvent;
import com.codeit.deokhugam.domain.notification.event.NotificationEventListener;
import com.codeit.deokhugam.domain.notification.event.PopularReviewSelectedEvent;
import com.codeit.deokhugam.domain.notification.event.ReviewLikedEvent;
import com.codeit.deokhugam.domain.notification.mapper.NotificationMapperImpl;
import com.codeit.deokhugam.domain.notification.repository.NotificationRepository;
import com.codeit.deokhugam.domain.notification.service.impl.NotificationServiceImpl;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.global.config.QueryDslConfig;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import({
	NotificationEventListener.class,
	NotificationServiceImpl.class,
	NotificationMapperImpl.class,
	QueryDslConfig.class
})
class NotificationEventIntegrationTest {

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private TestEntityManager entityManager;

	private User receiver;
	private User actor;
	private Review review;

	@BeforeEach
	void setUp() {
		receiver = entityManager.persist(new User(
			"receiver@test.com",
			"receiver",
			"password"
		));
		actor = entityManager.persist(new User(
			"actor@test.com",
			"actor",
			"password"
		));

		Book book = entityManager.persist(Book.builder()
			.title("title")
			.author("author")
			.description("description")
			.publisher("publisher")
			.publishedDate(LocalDate.of(2024, 1, 1))
			.isbn("9781234567890")
			.thumbnailUrl("thumbnail-url")
			.reviewCount(0L)
			.rating(0.0)
			.build());

		review = entityManager.persist(Review.builder()
			.content("review content")
			.rating(5)
			.likeCount(0L)
			.commentCount(0L)
			.book(book)
			.user(receiver)
			.build());

		entityManager.flush();
		entityManager.clear();
	}

	@Test
	@DisplayName("댓글 생성 이벤트가 커밋되면 알림을 저장한다")
	void publishCommentCreatedEvent_success() {
		eventPublisher.publishEvent(new CommentCreatedEvent(review.getId(), actor.getId()));

		assertThat(notificationRepository.findAll()).isEmpty();

		commitAndStartNewTransaction();

		List<Notification> notifications = notificationRepository.findAll();

		assertThat(notifications).hasSize(1);
		assertThat(notifications.get(0).getUser().getId()).isEqualTo(receiver.getId());
		assertThat(notifications.get(0).getReview().getId()).isEqualTo(review.getId());
		assertThat(notifications.get(0).getMessage()).contains("actor");
		assertThat(notifications.get(0).isConfirmed()).isFalse();
	}

	@Test
	@DisplayName("리뷰 좋아요 이벤트가 커밋되면 알림을 저장한다")
	void publishReviewLikedEvent_success() {
		eventPublisher.publishEvent(new ReviewLikedEvent(review.getId(), actor.getId()));

		assertThat(notificationRepository.findAll()).isEmpty();

		commitAndStartNewTransaction();

		List<Notification> notifications = notificationRepository.findAll();

		assertThat(notifications).hasSize(1);
		assertThat(notifications.get(0).getUser().getId()).isEqualTo(receiver.getId());
		assertThat(notifications.get(0).getReview().getId()).isEqualTo(review.getId());
		assertThat(notifications.get(0).getMessage()).contains("actor");
		assertThat(notifications.get(0).isConfirmed()).isFalse();
	}

	@Test
	@DisplayName("인기 리뷰 선정 이벤트가 커밋되면 알림을 저장한다")
	void publishPopularReviewSelectedEvent_success() {
		eventPublisher.publishEvent(new PopularReviewSelectedEvent(review.getId(), PeriodType.WEEKLY));

		assertThat(notificationRepository.findAll()).isEmpty();

		commitAndStartNewTransaction();

		List<Notification> notifications = notificationRepository.findAll();

		assertThat(notifications).hasSize(1);
		assertThat(notifications.get(0).getUser().getId()).isEqualTo(receiver.getId());
		assertThat(notifications.get(0).getReview().getId()).isEqualTo(review.getId());
		assertThat(notifications.get(0).getMessage()).contains("receiver");
		assertThat(notifications.get(0).isConfirmed()).isFalse();
	}

	private void commitAndStartNewTransaction() {
		TestTransaction.flagForCommit();
		TestTransaction.end();
		TestTransaction.start();
	}
}
