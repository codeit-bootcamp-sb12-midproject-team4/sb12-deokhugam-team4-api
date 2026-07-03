package com.codeit.deokhugam.domain.notification.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.notification.entity.Notification;
import com.codeit.deokhugam.domain.notification.mapper.NotificationMapperImpl;
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
public class NotificationRepositoryTest {

	private User user;
	private User otherUser;
	private Review review;
	private Notification notification1;
	private Notification notification2;
	private Notification notification3;
	private Notification otherUserNotification;

	@Autowired
	NotificationRepository notificationRepository;

	@Autowired
	TestEntityManager entityManager;

	@BeforeEach
	void setUp() {
		user = entityManager.persist(new User(
			"user@test.com",
			"user",
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
			.build()
		);

		review = entityManager.persist(Review.builder()
			.content("review content")
			.rating(5)
			.likeCount(0L)
			.commentCount(0L)
			.book(book)
			.user(user)
			.build()
		);

		notification1 = entityManager.persist(Notification.builder()
			.user(user)
			.review(review)
			.message("notification1")
			.build()
		);

		notification2 = entityManager.persist(Notification.builder()
			.user(user)
			.review(review)
			.message("notification2")
			.build()
		);

		notification3 = entityManager.persist(Notification.builder()
			.user(user)
			.review(review)
			.message("notification3")
			.build()
		);

		otherUserNotification = entityManager.persist(Notification.builder()
			.user(otherUser)
			.review(review)
			.message("otherUserNotification")
			.build()
		);

		updateCreatedAt(notification1, "2024-01-01T00:00:00Z");
		updateCreatedAt(notification2, "2024-01-02T00:00:00Z");
		updateCreatedAt(notification3, "2024-01-03T00:00:00Z");
		updateCreatedAt(otherUserNotification, "2024-01-04T00:00:00Z");
		entityManager.clear();
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

	@Test
	@DisplayName("알림 저장 성공")
	public void save_success() {
		Notification notification = Notification.builder()
			.user(user)
			.review(review)
			.message("saved notification")
			.build();

		Notification savedNotification = notificationRepository.save(notification);
		entityManager.flush();
		entityManager.clear();

		Notification result = notificationRepository.findById(savedNotification.getId()).orElseThrow();

		assertThat(result.getId()).isNotNull();
		assertThat(result.getUser().getId()).isEqualTo(user.getId());
		assertThat(result.getReview().getId()).isEqualTo(review.getId());
		assertThat(result.getMessage()).isEqualTo("saved notification");
		assertThat(result.isConfirmed()).isFalse();
	}

	@Test
	@DisplayName("알림 조회 성공")
	public void findByIdAndUserId_success() {
		Optional<Notification> result = notificationRepository.findByIdAndUserId(
			notification1.getId(),
			user.getId()
		);

		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(notification1.getId());
		assertThat(result.get().getUser().getId()).isEqualTo(user.getId());
	}

	@Test
	@DisplayName("단건 조회 실패_본인의 알림이 아님")
	public void findByIdAndUserId_failed() {
		Optional<Notification> result = notificationRepository.findByIdAndUserId(
			notification1.getId(),
			otherUser.getId()
		);

		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("전체 읽음 처리")
	public void markAllNotificationsAsRead_success() {
		Instant now = Instant.parse("2024-01-01T00:00:00Z");

		notificationRepository.markAllNotificationsAsRead(user.getId(), now);
		entityManager.flush();
		entityManager.clear();

		List<Notification> userNotificationList = notificationRepository.findAllByUserIdWithCursorASC(
			user.getId(),
			null,
			PageRequest.of(0, 10, Sort.by(Direction.ASC, "createdAt"))
		);
		Notification otherNotification = notificationRepository.findById(otherUserNotification.getId()).orElseThrow();

		assertThat(userNotificationList).hasSize(3);
		assertThat(userNotificationList)
			.allSatisfy(notification -> {
				assertThat(notification.isConfirmed()).isTrue();
				assertThat(notification.getUpdatedAt()).isEqualTo(now);
			});
		assertThat(otherNotification.isConfirmed()).isFalse();
		assertThat(otherNotification.getUpdatedAt()).isNotEqualTo(now);
	}

	@Test
	@DisplayName("알림 리스트 반환 테스트 DESC")
	public void findAllByUserIdWithCursorDESC() {
		Pageable pageable = PageRequest.of(
			0,
			2,
			Sort.by(Direction.DESC, "createdAt")
		);

		List<Notification> notificationList = notificationRepository.findAllByUserIdWithCursorDESC(
			user.getId(),
			null,
			pageable
		);

		assertThat(notificationList).hasSize(2);
		assertThat(notificationList)
			.extracting(Notification::getId)
			.containsExactly(
				notification3.getId(),
				notification2.getId()
			);
		// 다른 사용자가 포함이 되있는지 추출후 비교
		assertThat(notificationList)
			.extracting(notification -> notification.getUser().getId())
			.containsOnly(user.getId());
	}

	@Test
	@DisplayName("알림 리스트 반환 테스트 ASC")
	public void findAllByUserIdWithCursorASC() {
		Pageable pageable = PageRequest.of(
			0,
			2,
			Sort.by(Direction.ASC, "createdAt")
		);

		List<Notification> notificationList = notificationRepository.findAllByUserIdWithCursorASC(
			user.getId(),
			null,
			pageable
		);

		assertThat(notificationList).hasSize(2);
		assertThat(notificationList)
			.extracting(Notification::getId)
			.containsExactly(
				notification1.getId(),
				notification2.getId()
			);
		assertThat(notificationList)
			.extracting(notification -> notification.getUser().getId())
			.containsOnly(user.getId());
	}

	@Test
	@DisplayName("DESC 커서보다 오래된 알림만 조회한다")
	public void findAllByUserIdWithCursorDESC_onlyBeforeCursor() {
		Pageable pageable = PageRequest.of(
			0,
			10,
			Sort.by(Direction.DESC, "createdAt")
		);

		List<Notification> notificationList = notificationRepository.findAllByUserIdWithCursorDESC(
			user.getId(),
			Instant.parse("2024-01-03T00:00:00Z"),
			pageable
		);

		assertThat(notificationList)
			.extracting(Notification::getId)
			.containsExactly(
				notification2.getId(),
				notification1.getId()
			);
	}

	@Test
	@DisplayName("ASC 커서보다 최신 알림만 조회한다")
	public void findAllByUserIdWithCursorASC_onlyAfterCursor() {
		Pageable pageable = PageRequest.of(
			0,
			10,
			Sort.by(Direction.ASC, "createdAt")
		);

		List<Notification> notificationList = notificationRepository.findAllByUserIdWithCursorASC(
			user.getId(),
			Instant.parse("2024-01-01T00:00:00Z"),
			pageable
		);

		assertThat(notificationList)
			.extracting(Notification::getId)
			.containsExactly(
				notification2.getId(),
				notification3.getId()
			);
	}

	@Test
	@DisplayName("사용자별 알림 수를 조회한다")
	public void countByUserId_success() {
		long count = notificationRepository.countByUserId(user.getId());
		long otherUserCount = notificationRepository.countByUserId(otherUser.getId());

		assertThat(count).isEqualTo(3);
		assertThat(otherUserCount).isEqualTo(1);
	}

	// 이 메서드는 배치로 진행.
	@Test
	@DisplayName("읽음 처리된 알림 삭제 테스트")
	public void deleteConfirmedNotificationsAfterWeek() {
		Optional<Notification> notification = notificationRepository.findByIdAndUserId(
			notification1.getId(),
			user.getId()
		);

		assertThat(notification).isPresent();
		assertThat(notification.get().isConfirmed()).isFalse();

		notification.get().confirm();
		assertThat(notification.get().isConfirmed()).isTrue();
		entityManager.flush();

		Instant threshold = Instant.now()
			.plus(7, ChronoUnit.DAYS)
			.plus(1, ChronoUnit.MINUTES);
		int deletedCount = notificationRepository.deleteConfirmedAfter(threshold);
		entityManager.flush();
		entityManager.clear();

		assertThat(deletedCount).isEqualTo(1);
		assertThat(notificationRepository.findById(notification1.getId())).isEmpty();
		assertThat(notificationRepository.findById(notification2.getId())).isPresent();
		assertThat(notificationRepository.findById(notification3.getId())).isPresent();
		assertThat(notificationRepository.findById(otherUserNotification.getId())).isPresent();
	}
}
