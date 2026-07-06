package com.codeit.deokhugam.domain.notification.service;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.notification.dto.NotificationResponse;
import com.codeit.deokhugam.domain.notification.entity.Notification;
import com.codeit.deokhugam.domain.notification.event.CommentCreatedEvent;
import com.codeit.deokhugam.domain.notification.mapper.NotificationMapperImpl;
import com.codeit.deokhugam.domain.notification.repository.NotificationRepository;
import com.codeit.deokhugam.domain.notification.service.impl.NotificationServiceImpl;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.global.config.QueryDslConfig;

@DataJpaTest
@DirtiesContext
@ActiveProfiles("test")
@Import({
	NotificationServiceImpl.class,
	NotificationMapperImpl.class,
	QueryDslConfig.class
})
public class NotificationServiceTest {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private TestEntityManager entityManager;

	private User receiver;
	private User actor;
	private Review review;

	@BeforeEach
	void setUp() {
		deleteCommittedTestData();

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
			.thumbnailKey("thumbnail-key")
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

		TestTransaction.flagForCommit();
		TestTransaction.end();
		TestTransaction.start();
	}

	@Test
	@DisplayName("댓글 생성 알림을 저장한다")
	void saveCommentCreatedNotification_success() {
		CommentCreatedEvent event = new CommentCreatedEvent(review.getId(), actor.getId());

		notificationService.saveCommentCreatedNotification(event);
		entityManager.flush();
		entityManager.clear();

		//    이전 event에서 이벤트 생성 후 리스너로 실행하는 것을 체크 하였으니, 여기서는 서비스 로직 체크.

		List<Notification> notifications = notificationRepository.findAll();

		assertThat(notifications).hasSize(1);
		assertThat(notifications.get(0).getUser().getId()).isEqualTo(receiver.getId());
		assertThat(notifications.get(0).getReview().getId()).isEqualTo(review.getId());
		assertThat(notifications.get(0).isConfirmed()).isFalse();

		System.out.println("[created notification]");
		System.out.printf("receiverId=%s reviewId=%s message=%s confirmed=%s%n",
			notifications.get(0).getUser().getId(),
			notifications.get(0).getReview().getId(),
			notifications.get(0).getMessage(),
			notifications.get(0).isConfirmed()
		);
	}

	@Test
	@DisplayName("알림을 읽음 처리하고 응답을 반환한다")
	void markNotificationAsRead_success() {
		Notification notification = entityManager.persist(Notification.builder()
			.user(receiver)
			.review(review)
			.message("message")
			.build());
		entityManager.flush();
		entityManager.clear();

		NotificationResponse response = notificationService.markNotificationAsRead(
			notification.getId(),
			receiver.getId()
		);
		entityManager.flush();
		entityManager.clear();

		Notification result = notificationRepository.findById(notification.getId()).orElseThrow();

		assertThat(result.isConfirmed()).isTrue();
		assertThat(response.id()).isEqualTo(notification.getId());
		assertThat(response.confirmed()).isTrue();

		System.out.println("[read notification response]");
		System.out.println(response);
	}

	@Test
	@DisplayName("알림 목록을 최신순 커서 페이지로 조회한다")
	void findByUserId_success() {
		Notification notification1 = entityManager.persist(Notification.builder()
			.user(receiver)
			.review(review)
			.message("notification1")
			.build());
		Notification notification2 = entityManager.persist(Notification.builder()
			.user(receiver)
			.review(review)
			.message("notification2")
			.build());
		Notification notification3 = entityManager.persist(Notification.builder()
			.user(receiver)
			.review(review)
			.message("notification3")
			.build());
		entityManager.flush();

		updateCreatedAt(notification1, "2024-01-01T00:00:00Z");
		updateCreatedAt(notification2, "2024-01-02T00:00:00Z");
		updateCreatedAt(notification3, "2024-01-03T00:00:00Z");
		entityManager.clear();

		CursorPageResponse<NotificationResponse> response = notificationService.findByUserId(
			receiver.getId(),
			Direction.DESC,
			null,
			null,
			2
		);

		assertThat(response.getContent()).hasSize(2);
		assertThat(response.isHasNext()).isTrue();
		assertThat(response.getTotalElements()).isEqualTo(3);
		assertThat(response.getNextCursor()).isEqualTo("2024-01-02T00:00:00Z");
		assertThat(response.getContent())
			.extracting(NotificationResponse::message)
			.containsExactly("notification3", "notification2");

		System.out.println("[notification page response - first]");
		System.out.printf("size=%d totalElements=%d hasNext=%s nextCursor=%s nextAfter=%s%n",
			response.getSize(),
			response.getTotalElements(),
			response.isHasNext(),
			response.getNextCursor(),
			response.getNextAfter()
		);
		response.getContent().forEach(System.out::println);

		CursorPageResponse<NotificationResponse> nextResponse = notificationService.findByUserId(
			receiver.getId(),
			Direction.DESC,
			response.getNextCursor(),
			response.getNextAfter(),
			2
		);

		assertThat(nextResponse.getContent()).hasSize(1);
		assertThat(nextResponse.isHasNext()).isFalse();
		assertThat(nextResponse.getTotalElements()).isEqualTo(3);
		assertThat(nextResponse.getNextCursor()).isNull();
		assertThat(nextResponse.getNextAfter()).isNull();
		assertThat(nextResponse.getContent())
			.extracting(NotificationResponse::message)
			.containsExactly("notification1");

		System.out.println("[notification page response - next]");
		System.out.printf("size=%d totalElements=%d hasNext=%s nextCursor=%s nextAfter=%s%n",
			nextResponse.getSize(),
			nextResponse.getTotalElements(),
			nextResponse.isHasNext(),
			nextResponse.getNextCursor(),
			nextResponse.getNextAfter()
		);
		nextResponse.getContent().forEach(System.out::println);
	}

	private void updateCreatedAt(Notification notification, String createdAt) {
		entityManager.getEntityManager()
			.createQuery("""
				update Notification n
				set n.createdAt = :createdAt
				where n.id = :id
				""")
			.setParameter("createdAt", Instant.parse(createdAt))
			.setParameter("id", notification.getId())
			.executeUpdate();
	}

	private void deleteCommittedTestData() {
		entityManager.getEntityManager()
			.createQuery("delete from Notification n")
			.executeUpdate();
		entityManager.getEntityManager()
			.createQuery("delete from ReviewLike rl")
			.executeUpdate();
		entityManager.getEntityManager()
			.createQuery("delete from Comment c")
			.executeUpdate();
		entityManager.getEntityManager()
			.createQuery("delete from Review r")
			.executeUpdate();
		entityManager.getEntityManager()
			.createQuery("delete from Book b")
			.executeUpdate();
		entityManager.getEntityManager()
			.createQuery("delete from User u")
			.executeUpdate();
	}
}
