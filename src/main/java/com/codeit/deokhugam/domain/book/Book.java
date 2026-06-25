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

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, length = 50)
	private String author;

	@Column(length = 1000)
	private String description;

	@Column(nullable = false, length = 50)
	private String publisher;

	private LocalDate publishedDate;

	@Column(nullable = false, length = 20)
	private String isbn;

	@Column(length = 100)
	private String thumbnailUrl;

	@Column(columnDefinition = "BIGINT DEFAULT 0")
	private Long reviewCount = 0L;

	@Column(columnDefinition = "DOUBLE DEFAULT 0")
	private Double rating = 0.0;
}