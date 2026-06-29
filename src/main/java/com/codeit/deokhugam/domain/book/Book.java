package com.codeit.deokhugam.domain.book;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.codeit.deokhugam.domain.common.SoftDeletableEntity;

@Entity
@Table(name = "book", uniqueConstraints = {
	@UniqueConstraint(name = "uk_book_isbn", columnNames = "isbn")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Book extends SoftDeletableEntity {

	@Column(name = "title", nullable = false, length = 255)
	private String title;

	@Column(name = "author", nullable = false, length = 50)
	private String author;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Column(name = "publisher", nullable = false, length = 50)
	private String publisher;

	@Column(name = "published_date")
	private LocalDate publishedDate;

	@Column(name = "isbn", unique = true, length = 20)
	private String isbn;

	@Column(name = "thumbnail_url", length = 255)
	private String thumbnailUrl;

	@Column(name = "review_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
	private Long reviewCount = 0L;

	@Column(name = "rating", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
	private Double rating = 0.0;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_category_id")
	private BookCategory bookCategory;
}