package com.codeit.deokhugam.domain.notification.integration;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;
import com.codeit.deokhugam.domain.notification.dto.NotificationResponse;
import com.codeit.deokhugam.domain.notification.entity.Notification;
import com.codeit.deokhugam.domain.notification.event.CommentCreatedEvent;
import com.codeit.deokhugam.domain.notification.event.PopularReviewSelectedEvent;
import com.codeit.deokhugam.domain.notification.event.ReviewLikedEvent;
import com.codeit.deokhugam.domain.notification.exception.NotificationNotFoundException;
import com.codeit.deokhugam.domain.notification.exception.NotificationNotOwnedException;
import com.codeit.deokhugam.domain.notification.mapper.NotificationMapperImpl;
import com.codeit.deokhugam.domain.notification.repository.NotificationRepository;
import com.codeit.deokhugam.domain.notification.service.NotificationService;
import com.codeit.deokhugam.domain.notification.service.impl.NotificationServiceImpl;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.global.config.QueryDslConfig;

@DataJpaTest
@ActiveProfiles("test")
@Import({
	NotificationServiceImpl.class,
	NotificationMapperImpl.class,
	QueryDslConfig.class
})
class NotificationServiceIntegrationTest {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private TestEntityManager entityManager;

	private User receiver;
	private User actor;
	private User otherUser;
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
		otherUser = entityManager.persist(new User(
			"other@test.com",
			"other",
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
	@DisplayName("댓글 생성 알림을 저장한다")
	void saveCommentCreatedNotification_success() {
		notificationService.saveCommentCreatedNotification(
			new CommentCreatedEvent(review.getId(), actor.getId())
		);
		entityManager.flush();
		entityManager.clear();

		List<Notification> notifications = notificationRepository.findAll();

		assertThat(notifications).hasSize(1);
		assertThat(notifications.get(0).getUser().getId()).isEqualTo(receiver.getId());
		assertThat(notifications.get(0).getReview().getId()).isEqualTo(review.getId());
		assertThat(notifications.get(0).getMessage()).contains("actor");
		assertThat(notifications.get(0).isConfirmed()).isFalse();
	}

	@Test
	@DisplayName("리뷰 좋아요 알림을 저장한다")
	void saveReviewLikedNotification_success() {
		notificationService.saveReviewLikedNotification(
			new ReviewLikedEvent(review.getId(), actor.getId())
		);
		entityManager.flush();
		entityManager.clear();

		List<Notification> notifications = notificationRepository.findAll();

		assertThat(notifications).hasSize(1);
		assertThat(notifications.get(0).getUser().getId()).isEqualTo(receiver.getId());
		assertThat(notifications.get(0).getReview().getId()).isEqualTo(review.getId());
		assertThat(notifications.get(0).getMessage()).contains("actor");
		assertThat(notifications.get(0).isConfirmed()).isFalse();
	}

	@Test
	@DisplayName("인기 리뷰 선정 알림을 저장한다")
	void savePopularReviewSelectedNotification_success() {
		notificationService.savePopularReviewSelectedNotification(
			new PopularReviewSelectedEvent(review.getId(), PeriodType.WEEKLY)
		);
		entityManager.flush();
		entityManager.clear();

		List<Notification> notifications = notificationRepository.findAll();

		assertThat(notifications).hasSize(1);
		assertThat(notifications.get(0).getUser().getId()).isEqualTo(receiver.getId());
		assertThat(notifications.get(0).getReview().getId()).isEqualTo(review.getId());
		assertThat(notifications.get(0).getMessage()).contains("receiver");
		assertThat(notifications.get(0).isConfirmed()).isFalse();
	}

	@Test
	@DisplayName("알림 생성 대상 리뷰가 없으면 실패한다")
		// ReviewNotFoundException
	void saveNotification_reviewNotFound() {
		assertThatThrownBy(() -> notificationService.saveCommentCreatedNotification(
			new CommentCreatedEvent(UUID.randomUUID(), actor.getId())
		))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("review not found");

		assertThat(notificationRepository.findAll()).isEmpty();
	}

	@Test
	@DisplayName("댓글 작성 사용자가 없으면 알림 생성에 실패한다")
		// UserNotFoundException
	void saveCommentCreatedNotification_commentedUserNotFound() {
		assertThatThrownBy(() -> notificationService.saveCommentCreatedNotification(
			new CommentCreatedEvent(review.getId(), UUID.randomUUID())
		))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("commented user not found");

		assertThat(notificationRepository.findAll()).isEmpty();
	}

	@Test
	@DisplayName("알림 생성 대상 리뷰가 없으면 실패한다")
		// ReviewNotFoundException
	void saveReviewLikedNotification_reviewNotFound() {
		assertThatThrownBy(() -> notificationService.saveReviewLikedNotification(
			new ReviewLikedEvent(UUID.randomUUID(), actor.getId())
		))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("review not found");

		assertThat(notificationRepository.findAll()).isEmpty();
	}

	@Test
	@DisplayName("좋아요 누른 사용자가 없으면 알림 생성에 실패한다")
		// UserNotFoundException
	void saveReviewLikedNotification_likedUserNotFound() {
		assertThatThrownBy(() -> notificationService.saveReviewLikedNotification(
			new ReviewLikedEvent(review.getId(), UUID.randomUUID())
		))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("liked user not found");

		assertThat(notificationRepository.findAll()).isEmpty();
	}

	@Test
	@DisplayName("알림 생성 대상 리뷰가 없으면 실패한다")
		// ReviewNotFoundException
	void savePopularReviewSelectedNotification_reviewNotFound() {
		assertThatThrownBy(() -> notificationService.savePopularReviewSelectedNotification(
			new PopularReviewSelectedEvent(UUID.randomUUID(), PeriodType.WEEKLY)
		))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("review not found");

		assertThat(notificationRepository.findAll()).isEmpty();
	}

	@Test
	@DisplayName("알림을 단건 읽음 처리한다")
	void markNotificationAsRead_success() {
		Notification notification = saveNotification(receiver, "message", "2024-01-01T00:00:00Z");

		NotificationResponse response = notificationService.markNotificationAsRead(
			notification.getId(),
			receiver.getId()
		);
		entityManager.flush();
		entityManager.clear();

		Notification result = notificationRepository.findById(notification.getId()).orElseThrow();

		assertThat(result.isConfirmed()).isTrue();
		assertThat(response.id()).isEqualTo(notification.getId());
		assertThat(response.userId()).isEqualTo(receiver.getId());
		assertThat(response.reviewId()).isEqualTo(review.getId());
		assertThat(response.confirmed()).isTrue();
	}

	@Test
	@DisplayName("없는 알림을 읽음 처리하면 실패한다")
	void markNotificationAsRead_notFound() {
		assertThatThrownBy(() -> notificationService.markNotificationAsRead(
			UUID.randomUUID(),
			receiver.getId()
		))
			.isInstanceOf(NotificationNotFoundException.class);
	}

	@Test
	@DisplayName("다른 사용자의 알림을 읽음 처리하면 실패하고 상태를 변경하지 않는다")
	void markNotificationAsRead_notOwned() {
		Notification notification = saveNotification(receiver, "message", "2024-01-01T00:00:00Z");

		assertThatThrownBy(() -> notificationService.markNotificationAsRead(
			notification.getId(),
			otherUser.getId()
		))
			.isInstanceOf(NotificationNotOwnedException.class);
		entityManager.clear();

		Notification result = notificationRepository.findById(notification.getId()).orElseThrow();
		assertThat(result.isConfirmed()).isFalse();
	}

	@Test
	@DisplayName("이미 읽은 알림을 다시 읽음 처리해도 수정 시간이 바뀌지 않는다")
	void markNotificationAsRead_alreadyReadDoesNotUpdateAgain() {
		Notification notification = saveNotification(receiver, "message", "2024-01-01T00:00:00Z");

		notificationService.markNotificationAsRead(notification.getId(), receiver.getId());
		entityManager.flush();
		entityManager.clear();
		Instant firstUpdatedAt = notificationRepository.findById(notification.getId()).orElseThrow().getUpdatedAt();

		notificationService.markNotificationAsRead(notification.getId(), receiver.getId());
		entityManager.flush();
		entityManager.clear();
		Notification result = notificationRepository.findById(notification.getId()).orElseThrow();

		assertThat(result.isConfirmed()).isTrue();
		assertThat(result.getUpdatedAt()).isEqualTo(firstUpdatedAt);
	}

	@Test
	@DisplayName("알림 목록 전체 확인")
	void markAllNotificationsAsRead_success() {
		Notification notification1 = saveNotification(receiver, "notification1", "2024-01-01T00:00:00Z");
		Notification notification2 = saveNotification(receiver, "notification2", "2024-01-02T00:00:00Z");
		Notification otherNotification = saveNotification(otherUser, "other notification", "2024-01-03T00:00:00Z");

		notificationService.markAllNotificationsAsRead(receiver.getId());
		entityManager.flush();
		entityManager.clear();

		Notification result1 = notificationRepository.findById(notification1.getId()).orElseThrow();
		Notification result2 = notificationRepository.findById(notification2.getId()).orElseThrow();
		Notification otherResult = notificationRepository.findById(otherNotification.getId()).orElseThrow();

		assertThat(result1.isConfirmed()).isTrue();
		assertThat(result2.isConfirmed()).isTrue();
		assertThat(otherResult.isConfirmed()).isFalse();
	}

	@Test
	@DisplayName("목록 조회 기본값은 최신순이고 limit만큼 반환한다")
	void findByUserId_nullDirectionAndCursor() {
		saveNotification(receiver, "notification1", "2024-01-01T00:00:00Z");
		saveNotification(receiver, "notification2", "2024-01-02T00:00:00Z");
		saveNotification(receiver, "notification3", "2024-01-03T00:00:00Z");

		CursorPageResponse<NotificationResponse> response = notificationService.findByUserId(
			receiver.getId(),
			null,
			null,
			null,
			2
		);

		assertThat(response.getContent())
			.extracting(NotificationResponse::message)
			.containsExactly("notification3", "notification2");
		assertThat(response.getSize()).isEqualTo(2);
		assertThat(response.getTotalElements()).isEqualTo(3);
		assertThat(response.isHasNext()).isTrue();
		assertThat(response.getNextCursor()).isEqualTo("2024-01-02T00:00:00Z");
		assertThat(response.getNextAfter()).isEqualTo(Instant.parse("2024-01-02T00:00:00Z"));
	}

	@Test
	@DisplayName("목록 조회 결과가 limit 이하이면 다음 커서를 반환하지 않는다")
	void findByUserId_withoutNextCursor() {
		saveNotification(receiver, "notification1", "2024-01-01T00:00:00Z");
		saveNotification(receiver, "notification2", "2024-01-02T00:00:00Z");

		CursorPageResponse<NotificationResponse> response = notificationService.findByUserId(
			receiver.getId(),
			Direction.DESC,
			null,
			null,
			2
		);

		assertThat(response.getContent())
			.extracting(NotificationResponse::message)
			.containsExactly("notification2", "notification1");
		assertThat(response.getSize()).isEqualTo(2);
		assertThat(response.getTotalElements()).isEqualTo(2);
		assertThat(response.isHasNext()).isFalse();
		assertThat(response.getNextCursor()).isNull();
		assertThat(response.getNextAfter()).isNull();
	}

	@Test
	@DisplayName("목록 조회는 ASC 방향을 지원한다")
	void findByUserId_asc() {
		saveNotification(receiver, "notification1", "2024-01-01T00:00:00Z");
		saveNotification(receiver, "notification2", "2024-01-02T00:00:00Z");
		saveNotification(receiver, "notification3", "2024-01-03T00:00:00Z");

		CursorPageResponse<NotificationResponse> response = notificationService.findByUserId(
			receiver.getId(),
			Direction.ASC,
			"2024-01-01T00:00:00Z",
			Instant.parse("2024-01-01T00:00:00Z"),
			2
		);

		assertThat(response.getContent())
			.extracting(NotificationResponse::message)
			.containsExactly("notification2", "notification3");
		assertThat(response.getSize()).isEqualTo(2);
		assertThat(response.getTotalElements()).isEqualTo(3);
		assertThat(response.isHasNext()).isFalse();
		assertThat(response.getNextCursor()).isNull();
		assertThat(response.getNextAfter()).isNull();
	}

	@Test
	@DisplayName("알림이 없으면 빈 목록을 반환한다")
	void findByUserId_empty() {
		CursorPageResponse<NotificationResponse> response = notificationService.findByUserId(
			otherUser.getId(),
			null,
			null,
			null,
			2
		);

		assertThat(response.getContent()).isEmpty();
		assertThat(response.getSize()).isZero();
		assertThat(response.getTotalElements()).isZero();
		assertThat(response.isHasNext()).isFalse();
		assertThat(response.getNextCursor()).isNull();
		assertThat(response.getNextAfter()).isNull();
	}

	private Notification saveNotification(User user, String message, String createdAt) {
		Notification notification = entityManager.persist(Notification.builder()
			.user(user)
			.review(review)
			.message(message)
			.build());
		entityManager.flush();
		updateCreatedAt(notification, createdAt);
		entityManager.clear();
		return notification;
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
}
