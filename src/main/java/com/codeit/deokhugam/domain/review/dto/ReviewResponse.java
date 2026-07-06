package com.codeit.deokhugam.domain.review.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
	private UUID id;
	private UUID bookId;
	private String bookTitle;
	@Setter
	private String bookThumbnailUrl;
	private UUID userId;
	private String userNickname;
	private String content;
	@Setter
	private String attachmentUrl;
	private Integer rating;
	private Long likeCount;
	private Long commentCount;
	private boolean likedByMe;
	private Instant createdAt;
	private Instant updatedAt;
}
