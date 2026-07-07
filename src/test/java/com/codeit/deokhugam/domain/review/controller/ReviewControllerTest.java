package com.codeit.deokhugam.domain.review.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.review.dto.ReviewResponse;
import com.codeit.deokhugam.domain.review.service.ReviewService;
import com.codeit.deokhugam.domain.reviewlike.dto.ReviewLikeResponse;

import jakarta.servlet.ServletException;

@SpringBootTest(properties = {
	"spring.sql.init.mode=never",
	"spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class ReviewControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReviewService reviewService;

	private final UUID reviewId = UUID.randomUUID();
	private final UUID bookId = UUID.randomUUID();
	private final UUID userId = UUID.randomUUID();

	@Test
	@DisplayName("리뷰 등록 성공")
	void createReview_success() throws Exception {
		ReviewResponse response = ReviewResponse.builder()
			.id(reviewId)
			.content("테스트 리뷰")
			.rating(5)
			.likedByMe(false)
			.build();

		given(reviewService.save(any(), isNull())).willReturn(response);

		MockMultipartFile request = new MockMultipartFile(
			"request", "", "application/json",
			"""
				{
				    "bookId": "%s",
				    "userId": "%s",
				    "content": "테스트 리뷰",
				    "rating": 5
				}
				""".formatted(bookId, userId).getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(multipart("/api/reviews")
				.file(request))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.content").value("테스트 리뷰"))
			.andExpect(jsonPath("$.rating").value(5));
	}

	@Test
	@DisplayName("리뷰 등록 실패 - 유효성 검사")
	void createReview_fail() throws Exception {
		mockMvc.perform(post("/api/reviews")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					    "bookId": "%s",
					    "userId": "%s",
					    "content": "",
					    "rating": 10
					}
					""".formatted(bookId, userId)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("리뷰 단건 조회 성공")
	void getReview_success() throws Exception {
		ReviewResponse response = ReviewResponse.builder()
			.id(reviewId)
			.content("테스트 리뷰")
			.rating(5)
			.likedByMe(false)
			.build();

		given(reviewService.findByReviewId(reviewId, userId)).willReturn(response);

		mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
				.header("Deokhugam-Request-User-ID", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value("테스트 리뷰"));
	}

	@Test
	@DisplayName("리뷰 단건 조회 실패 - 존재하지 않는 리뷰")
	void getReview_fail() {
		given(reviewService.findByReviewId(any(), any()))
			.willThrow(new NoSuchElementException("리뷰를 찾을 수 없습니다."));

		assertThrows(ServletException.class, () ->
			mockMvc.perform(get("/api/reviews/{reviewId}", reviewId)
				.header("Deokhugam-Request-User-ID", userId.toString()))
		);
	}

	@Test
	@DisplayName("리뷰 목록 조회 성공")
	void getReviews_success() throws Exception {
		CursorPageResponse<ReviewResponse> response = CursorPageResponse.<ReviewResponse>builder()
			.content(List.of())
			.totalElements(0L)
			.hasNext(false)
			.size(0)
			.build();

		given(reviewService.findByRequest(any())).willReturn(response);

		mockMvc.perform(get("/api/reviews")
				.header("Deokhugam-Request-User-ID", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalElements").value(0));
	}

	@Test
	@DisplayName("리뷰 수정 성공")
	void updateReview_success() throws Exception {
		ReviewResponse response = ReviewResponse.builder()
			.id(reviewId)
			.content("수정된 리뷰")
			.rating(3)
			.likedByMe(false)
			.build();

		given(reviewService.update(eq(reviewId), eq(userId), any(), isNull())).willReturn(response);

		MockMultipartFile request = new MockMultipartFile(
			"request", "", "application/json",
			"""
				{
				    "content": "수정된 리뷰",
				    "rating": 3
				}
				""".getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(multipart(HttpMethod.PATCH, "/api/reviews/{reviewId}", reviewId)
				.file(request)
				.header("Deokhugam-Request-User-ID", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value("수정된 리뷰"));
	}

	@Test
	@DisplayName("리뷰 논리 삭제 성공")
	void softDeleteReview_success() throws Exception {
		willDoNothing().given(reviewService).deleteReview(reviewId, userId);

		mockMvc.perform(delete("/api/reviews/{reviewId}", reviewId)
				.header("Deokhugam-Request-User-ID", userId))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("리뷰 물리 삭제 성공")
	void hardDeleteReview_success() throws Exception {
		willDoNothing().given(reviewService).hardDeleteReview(reviewId, userId);

		mockMvc.perform(delete("/api/reviews/{reviewId}/hard", reviewId)
				.header("Deokhugam-Request-User-ID", userId))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("좋아요 토글 성공")
	void createLike_success() throws Exception {
		ReviewLikeResponse response = ReviewLikeResponse.builder()
			.reviewId(reviewId)
			.userId(userId)
			.liked(true)
			.build();

		given(reviewService.toggleLike(reviewId, userId)).willReturn(response);

		mockMvc.perform(post("/api/reviews/{reviewId}/like", reviewId)
				.header("Deokhugam-Request-User-ID", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.liked").value(true));
	}

	@Test
	@DisplayName("좋아요한 리뷰 목록 조회 성공")
	void getLikedReviews_success() throws Exception {
		CursorPageResponse<ReviewResponse> response = CursorPageResponse.<ReviewResponse>builder()
			.content(List.of())
			.totalElements(0L)
			.hasNext(false)
			.size(0)
			.build();

		given(reviewService.findLikedReviews(any(), eq(userId))).willReturn(response);

		mockMvc.perform(get("/api/reviews/like/{userId}", userId)
				.header("Deokhugam-Request-User-ID", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalElements").value(0));
	}
}