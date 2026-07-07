package com.codeit.deokhugam.domain.book.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.util.StringUtils;

import com.codeit.deokhugam.domain.booksearch.BookDocument;
import com.codeit.deokhugam.domain.bookstatus.BookStatusType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
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
	private BookStatusType status;
	private Instant createdAt;
	private Instant updatedAt;

	public static BookResponse from(BookDocument doc) {
		return BookResponse.builder()
			.id(UUID.fromString(doc.getId()))
			.title(doc.getTitle())
			.author(doc.getAuthor())
			.description(doc.getDescription())
			.publisher(doc.getPublisher())
			.isbn(doc.getIsbn())
			.thumbnailUrl(doc.getThumbnailKey() != null ? doc.getThumbnailKey() : null)
			.reviewCount(doc.getReviewCount())
			.rating(doc.getRating())
			.publishedDate(parseLocalDate(doc.getPublishedDate()))
			.categoryPath(doc.getCategoryPath())
			.createdAt(doc.getCreatedAt())
			.updatedAt(doc.getUpdatedAt())
			.build();
	}
	private static LocalDate parseLocalDate(String dateStr) {
		if (!StringUtils.hasText(dateStr)) {
			return null;
		}
		if (dateStr.contains("T")) {
			dateStr = dateStr.substring(0, dateStr.indexOf("T"));
		}
		return LocalDate.parse(dateStr);
	}
}
