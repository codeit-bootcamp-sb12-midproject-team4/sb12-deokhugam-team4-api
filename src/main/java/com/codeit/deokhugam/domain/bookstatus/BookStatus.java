package com.codeit.deokhugam.domain.bookstatus;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.common.UpdatableEntity;
import com.codeit.deokhugam.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Getter
@Table(
	name = "book_status",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_book_status_user_book",
			columnNames = {"user_id", "book_id"}
		)
	})
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class BookStatus extends UpdatableEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 10, nullable = false)
	private BookStatusType status; // Enum 사용 권장

	public void updateStatus(BookStatusType status) {
		this.status = status;
	}
}
