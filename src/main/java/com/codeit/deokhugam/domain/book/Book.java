package com.codeit.deokhugam.domain.book;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.codeit.deokhugam.domain.common.SoftDeletableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "book", uniqueConstraints = {
	@UniqueConstraint(name = "uk_book_isbn", columnNames = "isbn")
})
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

	@Column(name = "thumbnail_key", length = 255)
	private String thumbnailKey;

	@Builder.Default
	@Column(name = "review_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
	private Long reviewCount = 0L;

	@Builder.Default
	@Column(name = "rating", nullable = false, columnDefinition = "DOUBLE DEFAULT 0")
	private Double rating = 0.0;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_category_id")
	private BookCategory bookCategory;

	public void update(String title, String author, String description, String publisher, LocalDate publishedDate, String thumbnailKey) {
		if (title != null)
			this.title = title;
		if (author != null)
			this.author = author;
		this.description = description;
		if (publisher != null)
			this.publisher = publisher;
		this.publishedDate = publishedDate;
		if (thumbnailKey != null)
			this.thumbnailKey = thumbnailKey;
	}

}