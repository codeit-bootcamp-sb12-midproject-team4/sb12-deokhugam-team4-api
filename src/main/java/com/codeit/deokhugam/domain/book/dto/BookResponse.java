package com.codeit.deokhugam.domain.book.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookResponse {
	private UUID id;
	private String title;
	private String author;
	private String description;
	private String publisher;
	private LocalDate publishedDate;
	private String isbn;
	private String thumbnailUrl;
	private Long reviewCount;
	private Double rating;
	private String categoryPath;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
