package com.codeit.deokhugam.domain.bookrecommendation;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.common.UpdatableEntity;
import com.codeit.deokhugam.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
	name = "book_recommendation",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_book_recommendation_user_book",
			columnNames = {"user_id", "book_id"}
		)
	}
)
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BookRecommendation extends UpdatableEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false, updatable = false)
	private Book book;

}
