package com.codeit.deokhugam.domain.notification.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.notification.entity.Notification;
import com.codeit.deokhugam.domain.notification.repository.NotificationRepository;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.user.User;

import jakarta.persistence.EntityManager;

@SpringBootTest(properties = {
	"spring.profiles.active=test",
	"spring.datasource.url=jdbc:h2:mem:notification-controller-test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
	"spring.datasource.driver-class-name=org.h2.Driver",
	"spring.datasource.username=sa",
	"spring.datasource.password=",
	"spring.sql.init.mode=never",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class NotificationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private NotificationRepository notificationRepository;

	private User user;
	private Notification notification1;
	private Notification notification2;
	private Notification notification3;

	@BeforeEach
	void setUp() {
		user = persist(new User(
			"notification-user@test.com",
			"notificationUser",
			"password"
		));

		Book book = persist(Book.builder()
			.title("title")
			.author("author")
			.description("description")
			.publisher("publisher")
			.publishedDate(LocalDate.of(2024, 1, 1))
			.isbn("9781234567890")
				.thumbnailKey("thumbnail-key")
				.reviewCount(0L)
				.rating(0.0)
				.build()
		);

		Review review = persist(Review.builder()
			.content("review content")
			.rating(5)
			.likeCount(0L)
			.commentCount(0L)
			.book(book)
			.user(user)
			.build()
		);

		notification1 = persist(Notification.builder()
			.user(user)
			.review(review)
			.message("notification1")
			.build()
		);

		notification2 = persist(Notification.builder()
			.user(user)
			.review(review)
			.message("notification2")
			.build()
		);

		notification3 = persist(Notification.builder()
			.user(user)
			.review(review)
			.message("notification3")
			.build()
		);

		entityManager.flush();

		updateCreatedAt(notification1, "2024-01-01T00:00:00Z");
		updateCreatedAt(notification2, "2024-01-02T00:00:00Z");
		updateCreatedAt(notification3, "2024-01-03T00:00:00Z");

		entityManager.clear();
	}

	@Test
	@DisplayName("알림 목록을 조회하고 전체 응답을 출력한다")
	void getNotifications_success() throws Exception {
		mockMvc.perform(get("/api/notifications")
				.header("Deokhugam-Request-User-ID", user.getId())
				.param("direction", "DESC")
				.param("limit", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content[0].message").value("notification3"))
			.andExpect(jsonPath("$.content[1].message").value("notification2"))
			.andExpect(jsonPath("$.size").value(2))
			.andExpect(jsonPath("$.totalElements").value(3))
			.andExpect(jsonPath("$.hasNext").value(true))
			.andExpect(jsonPath("$.nextCursor").value("2024-01-02T00:00:00Z"));
	}

	@Test
	@DisplayName("알림을 읽음 처리하고 전체 응답을 출력한다")
	void markNotificationAsRead_success() throws Exception {
		mockMvc.perform(patch("/api/notifications/{notificationId}", notification1.getId())
				.header("Deokhugam-Request-User-ID", user.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "confirmed": true
					}
					"""))
			.andExpect(status().isOk());

		Notification result = notificationRepository.findById(notification1.getId()).orElseThrow();

		assertThat(result.isConfirmed()).isTrue();
	}

	@Test
	@DisplayName("전체 알림을 읽음 처리하고 전체 응답을 출력한다")
	void markAllNotificationsAsRead_success() throws Exception {
		mockMvc.perform(patch("/api/notifications/read-all")
				.header("Deokhugam-Request-User-ID", user.getId()))
			.andExpect(status().isOk());

		List<Notification> notifications = notificationRepository.findAll();

		assertThat(notifications).hasSize(3);
		assertThat(notifications)
			.allSatisfy(notification -> assertThat(notification.isConfirmed()).isTrue());
	}

	private void updateCreatedAt(Notification notification, String createdAt) {
		entityManager.createQuery("""
				update Notification n
				set n.createdAt = :createdAt
				where n.id = :id
				""")
			.setParameter("createdAt", Instant.parse(createdAt))
			.setParameter("id", notification.getId())
			.executeUpdate();
	}

	private <T> T persist(T entity) {
		entityManager.persist(entity);
		return entity;
	}
}
