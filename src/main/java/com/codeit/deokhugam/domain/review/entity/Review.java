package com.codeit.deokhugam.domain.review.entity;

import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.common.SoftDeletableEntity;
import com.codeit.deokhugam.domain.user.User;

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
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Table(
	name = "review",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_reviews_book_user",
			columnNames = {"book_id", "user_id"}
		)
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE review SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Review extends SoftDeletableEntity {

	@Column(name = "content", length = 1000, nullable = false)
	private String content;

	@Column(name = "attachment_url", length = 100)
	private String attachmentUrl;

	@Column(name = "rating", nullable = false)
	private Integer rating;

	@Builder.Default
	@Column(name = "like_count", nullable = false)
	private Long likeCount = 0L;

	@Builder.Default
	@Column(name = "comment_count", nullable = false)
	private Long commentCount = 0L;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public void update(String content, Integer rating) {
		this.content = content;
		this.rating = rating;
	}

	/// 동시성 이슈 발생지점! -> Repository Layer에서 @OptimisticLock으로 처리
	public void increaseLikeCount() {
		this.likeCount++;
	}

	public void decreaseLikeCount() {
		if (likeCount > 0) {
			this.likeCount--;
		}
	}

	public void increaseCommentCount() {
		this.commentCount++;
	}

	public void decreaseCommentCount() {
		if (commentCount > 0) {
			this.commentCount--;
		}
	}

	public boolean isOwnedBy(UUID userId) {
		return this.user.getId().equals(userId);
	}
}