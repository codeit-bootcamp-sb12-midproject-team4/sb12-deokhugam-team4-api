package com.codeit.deokhugam.domain.notification.integration;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.notification.entity.Notification;
import com.codeit.deokhugam.domain.notification.repository.NotificationRepository;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.user.User;

import jakarta.persistence.EntityManager;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class NotificationControllerIntegrationTest {

	private static final String REQUEST_USER_ID_HEADER = "Deokhugam-Request-User-ID";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private NotificationRepository notificationRepository;

	private User user;
	private User otherUser;
	private Review review;
	private Notification notification1;
	private Notification notification2;
	private Notification notification3;
	private Notification otherUserNotification;

	@BeforeEach
	void setUp() {
		user = persist(new User(
			"notification-user@test.com",
			"notificationUser",
			"password"
		));
		otherUser = persist(new User(
			"notification-other@test.com",
			"notificationOther",
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

		review = persist(Review.builder()
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
		otherUserNotification = persist(Notification.builder()
			.user(otherUser)
			.review(review)
			.message("otherUserNotification")
			.build()
		);

		entityManager.flush();

		updateCreatedAt(notification1, "2024-01-01T00:00:00Z");
		updateCreatedAt(notification2, "2024-01-02T00:00:00Z");
		updateCreatedAt(notification3, "2024-01-03T00:00:00Z");
		updateCreatedAt(otherUserNotification, "2024-01-04T00:00:00Z");
		entityManager.clear();
	}

	@Test
	@DisplayName("알림 목록을 조회한다")
	void getNotifications_success() throws Exception {
		mockMvc.perform(get("/api/notifications")
				.header(REQUEST_USER_ID_HEADER, user.getId())
				.param("direction", "DESC")
				.param("limit", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content[0].message").value("notification3"))
			.andExpect(jsonPath("$.content[1].message").value("notification2"))
			.andExpect(jsonPath("$.size").value(2))
			.andExpect(jsonPath("$.totalElements").value(3))
			.andExpect(jsonPath("$.hasNext").value(true))
			.andExpect(jsonPath("$.nextCursor").value("2024-01-02T00:00:00Z"))
			.andExpect(jsonPath("$.nextAfter").value("2024-01-02T00:00:00Z"));
	}

	@Test
	@DisplayName("알림 목록 조회는 기본값으로 최신순 20개를 조회한다")
	void getNotifications_defaultParams() throws Exception {
		mockMvc.perform(get("/api/notifications")
				.header(REQUEST_USER_ID_HEADER, user.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(3))
			.andExpect(jsonPath("$.content[0].message").value("notification3"))
			.andExpect(jsonPath("$.content[1].message").value("notification2"))
			.andExpect(jsonPath("$.content[2].message").value("notification1"))
			.andExpect(jsonPath("$.size").value(3))
			.andExpect(jsonPath("$.totalElements").value(3))
			.andExpect(jsonPath("$.hasNext").value(false))
			.andExpect(jsonPath("$.nextCursor").doesNotExist())
			.andExpect(jsonPath("$.nextAfter").doesNotExist());
	}

	@Test
	@DisplayName("알림 목록을 ASC 방향과 커서로 조회한다")
	void getNotifications_ascWithCursor() throws Exception {
		mockMvc.perform(get("/api/notifications")
				.header(REQUEST_USER_ID_HEADER, user.getId())
				.param("direction", "ASC")
				.param("cursor", "2024-01-01T00:00:00Z")
				.param("after", "2024-01-01T00:00:00Z")
				.param("limit", "2"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content[0].message").value("notification2"))
			.andExpect(jsonPath("$.content[1].message").value("notification3"))
			.andExpect(jsonPath("$.size").value(2))
			.andExpect(jsonPath("$.totalElements").value(3))
			.andExpect(jsonPath("$.hasNext").value(false))
			.andExpect(jsonPath("$.nextCursor").doesNotExist())
			.andExpect(jsonPath("$.nextAfter").doesNotExist());
	}

	@Test
	@DisplayName("알림 목록 조회 limit은 1 이상이어야 한다")
		// MethodValidationException 가능
	void getNotifications_limitMinValidation() throws Exception {
		mockMvc.perform(get("/api/notifications")
				.header(REQUEST_USER_ID_HEADER, user.getId())
				.param("limit", "0"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("알림 목록 조회 요청 헤더가 없으면 실패한다")
		// MissingRequestHeaderException
	void getNotifications_missingRequestUserHeader() throws Exception {
		mockMvc.perform(get("/api/notifications"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("알림 목록 조회 요청 헤더가 UUID 형식이 아니면 실패한다")
		// MethodArgumentTypeMismatchException
	void getNotifications_invalidRequestUserHeader() throws Exception {
		mockMvc.perform(get("/api/notifications")
				.header(REQUEST_USER_ID_HEADER, "invalid-uuid"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("알림을 단건 읽음 처리한다")
	void markNotificationAsRead_success() throws Exception {
		mockMvc.perform(patch("/api/notifications/{notificationId}", notification1.getId())
				.header(REQUEST_USER_ID_HEADER, user.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "confirmed": true
					}
					"""))
			.andExpect(status().isOk());

		entityManager.flush();
		entityManager.clear();
		Notification result = notificationRepository.findById(notification1.getId()).orElseThrow();

		assertThat(result.isConfirmed()).isTrue();
	}

	@Test
	@DisplayName("알림 단건 읽음 처리 요청의 confirmed는 필수다")
	void markNotificationAsRead_confirmedRequired() throws Exception {
		mockMvc.perform(patch("/api/notifications/{notificationId}", notification1.getId())
				.header(REQUEST_USER_ID_HEADER, user.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "confirmed": null
					}
					"""))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("없는 알림을 읽음 처리하면 404를 반환한다")
	void markNotificationAsRead_notFound() throws Exception {
		mockMvc.perform(patch("/api/notifications/{notificationId}", UUID.randomUUID())
				.header(REQUEST_USER_ID_HEADER, user.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "confirmed": true
					}
					"""))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("NOTIFICATION_NOT_FOUND"))
			.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	@DisplayName("다른 사용자의 알림을 읽음 처리하면 403을 반환한다")
	void markNotificationAsRead_notOwned() throws Exception {
		mockMvc.perform(patch("/api/notifications/{notificationId}", notification1.getId())
				.header(REQUEST_USER_ID_HEADER, otherUser.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "confirmed": true
					}
					"""))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.code").value("NOTIFICATION_NOT_OWNED"))
			.andExpect(jsonPath("$.status").value(403));

		entityManager.clear();
		Notification result = notificationRepository.findById(notification1.getId()).orElseThrow();
		assertThat(result.isConfirmed()).isFalse();
	}

	@Test
	@DisplayName("알림 단건 읽음 처리 요청 헤더가 없으면 실패한다")
	void markNotificationAsRead_missingRequestUserHeader() throws Exception {
		mockMvc.perform(patch("/api/notifications/{notificationId}", notification1.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "confirmed": true
					}
					"""))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("알림 단건 읽음 처리 요청 헤더가 UUID 형식이 아니면 실패한다")
	void markNotificationAsRead_invalidRequestUserHeader() throws Exception {
		mockMvc.perform(patch("/api/notifications/{notificationId}", notification1.getId())
				.header(REQUEST_USER_ID_HEADER, "invalid-uuid")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "confirmed": true
					}
					"""))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("모든 알림을 읽음 처리한다")
	void markAllNotificationsAsRead_success() throws Exception {
		mockMvc.perform(patch("/api/notifications/read-all")
				.header(REQUEST_USER_ID_HEADER, user.getId()))
			.andExpect(status().isOk());

		entityManager.clear();
		List<Notification> userNotifications = notificationRepository.findAllById(List.of(
			notification1.getId(),
			notification2.getId(),
			notification3.getId()
		));
		Notification otherNotification = notificationRepository.findById(otherUserNotification.getId()).orElseThrow();

		assertThat(userNotifications).hasSize(3);
		assertThat(userNotifications)
			.allSatisfy(notification -> assertThat(notification.isConfirmed()).isTrue());
		assertThat(otherNotification.isConfirmed()).isFalse();
	}

	@Test
	@DisplayName("모든 알림 읽음 처리 요청 헤더가 없으면 실패한다")
	void markAllNotificationsAsRead_missingRequestUserHeader() throws Exception {
		mockMvc.perform(patch("/api/notifications/read-all"))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("모든 알림 읽음 처리 요청 헤더가 UUID 형식이 아니면 실패한다")
	void markAllNotificationsAsRead_invalidRequestUserHeader() throws Exception {
		mockMvc.perform(patch("/api/notifications/read-all")
				.header(REQUEST_USER_ID_HEADER, "invalid-uuid"))
			.andExpect(status().isBadRequest());
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
