package com.codeit.deokhugam.domain.review.repository.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.codeit.deokhugam.domain.book.QBook;
import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.review.dto.ReviewSearchCondition;
import com.codeit.deokhugam.domain.review.entity.QReview;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.review.repository.ReviewRepositoryCustom;
import com.codeit.deokhugam.domain.user.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private static final QReview review = QReview.review;
	private static final QBook book = QBook.book;
	private static final QUser user = QUser.user;

	@Override
	public CursorPageResponse<Review> findReviewsByCondition(ReviewSearchCondition condition) {

		int limit = condition.getLimit() > 0 ? condition.getLimit() : 50;

		BooleanBuilder baseWhere = new BooleanBuilder();

		// 완전 일치 조건
		if (condition.getUserId() != null) {
			baseWhere.and(
				review.user.id.eq(condition.getUserId())
			);
		}
		if (condition.getBookId() != null) {
			baseWhere.and(
				review.book.id.eq(condition.getBookId())
			);
		}

		// 부분 일치 조건
		if (condition.getKeyword() != null && !condition.getKeyword().isBlank()) {
			baseWhere.and(
				review.user.nickname.containsIgnoreCase(condition.getKeyword())
					.or(review.content.containsIgnoreCase(condition.getKeyword()))
					.or(review.book.title.containsIgnoreCase(condition.getKeyword()))
			);
		}

		BooleanBuilder where = new BooleanBuilder(baseWhere);

		// 커서 조건
		if (condition.getCursor() != null && condition.getAfter() != null) {
			if ("rating".equals(condition.getOrderBy())) {
				int rating = Integer.parseInt(condition.getCursor());
				if ("ASC".equals(condition.getDirection())) {
					where.and(
						review.rating.gt(rating)
							.or(review.rating.eq(rating)
								.and(review.createdAt.gt(condition.getAfter())))
					);
				} else {
					where.and(
						review.rating.lt(rating)
							.or(review.rating.eq(rating)
								.and(review.createdAt.lt(condition.getAfter())))
					);
				}
			} else { // default값: createdAt
				if ("ASC".equals(condition.getDirection())) {
					where.and(
						review.createdAt.gt(condition.getAfter())
					);
				} else {
					where.and(
						review.createdAt.lt(condition.getAfter())
					);
				}
			}
		}

		// 정렬 기준
		OrderSpecifier<?> orderSpecifier = getOrderSpecifier(
			condition.getOrderBy(), condition.getDirection()
		);

		// 조회
		List<Review> reviews = queryFactory
			.selectFrom(review)
			.join(review.book, book).fetchJoin()
			.join(review.user, user).fetchJoin()
			.where(where)
			.orderBy(orderSpecifier, review.createdAt.desc())
			.limit(limit + 1)
			.fetch();

		// hasNext
		boolean hasNext = reviews.size() > limit;
		if (hasNext) {
			reviews.remove(reviews.size() - 1);
		}

		// totalElements
		Long totalElements = queryFactory
			.select(review.count())
			.from(review)
			.join(review.book, book)
			.join(review.user, user)
			.where(baseWhere)
			.fetchOne();

		// 다음 커서
		String nextCursor = null;
		Instant nextAfter = null;
		if (hasNext && !reviews.isEmpty()) {
			Review lastReview = reviews.get(reviews.size() - 1);
			nextAfter = lastReview.getCreatedAt();
			if ("rating".equals(condition.getOrderBy())) {
				nextCursor = String.valueOf(lastReview.getRating());
			} else {
				nextCursor = lastReview.getCreatedAt().toString();
			}
		}

		return CursorPageResponse.<Review>builder()
			.content(reviews)
			.nextCursor(nextCursor)
			.nextAfter(nextAfter)
			.size(reviews.size())
			.totalElements(totalElements != null ? totalElements : 0L)
			.hasNext(hasNext)
			.build();
	}

	private OrderSpecifier<?> getOrderSpecifier(String orderBy, String direction) {
		boolean isAsc = "ASC".equals(direction);

		if ("rating".equals(orderBy)) {
			return isAsc ? review.rating.asc() : review.rating.desc();
		}
		return isAsc ? review.createdAt.asc() : review.createdAt.desc();
	}
}
