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

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.notification.entity.Notification;
import com.codeit.deokhugam.domain.notification.mapper.NotificationMapperImpl;
import com.codeit.deokhugam.domain.notification.service.impl.NotificationServiceImpl;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.global.config.QueryDslConfig;

@DataJpaTest(properties = {
	"spring.sql.init.mode=never",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
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

		updateCreatedAt(notification1, "2024-01-01T00:00:00Z");
		updateCreatedAt(notification2, "2024-01-02T00:00:00Z");
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
	@DisplayName("단건 조회 성공")
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
	@DisplayName("단건 조회 실패")
	public void findByIdAndUserId_failed() {
		Optional<Notification> result = notificationRepository.findByIdAndUserId(
			notification1.getId(),
			user.getId()
		);

		assertThat(result).isPresent();
		assertThat(result.get().getId()).isNotEqualTo(notification2.getId());
	}

	@Test
	@DisplayName("전체 읽음 처리")
	public void markAllNotificationsAsRead_success() {
		Instant now = Instant.parse("2024-01-01T00:00:00Z");

		notificationRepository.markAllNotificationsAsRead(user.getId(), now);
		entityManager.flush();
		entityManager.clear();

		List<Notification> notificationList = notificationRepository.findAll();

		assertThat(notificationList).hasSize(3);
		assertThat(notificationList)
			.allSatisfy(notification -> {
				assertThat(notification.isConfirmed()).isTrue();
				assertThat(notification.getUpdatedAt()).isEqualTo(now);
			});
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
	}

	@Test
	@DisplayName("삭제 테스트")
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
	}

}
