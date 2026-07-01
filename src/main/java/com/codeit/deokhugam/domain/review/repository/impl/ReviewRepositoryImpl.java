package com.codeit.deokhugam.domain.review.repository.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.codeit.deokhugam.domain.book.QBook;
import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.review.dto.LikedReviewSearchRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.codeit.deokhugam.domain.review.entity.QReview;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.review.repository.ReviewRepositoryCustom;
import com.codeit.deokhugam.domain.reviewlike.entity.QReviewLike;
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
	private static final QReviewLike reviewLike = QReviewLike.reviewLike;

	@Override
	public CursorPageResponse<Review> findReviewsByRequest(ReviewSearchRequest request) {

		int limit = request.getLimit() > 0 ? request.getLimit() : 50;

		BooleanBuilder baseWhere = new BooleanBuilder();

		// 완전 일치 조건
		if (request.getUserId() != null) {
			baseWhere.and(
				review.user.id.eq(request.getUserId())
			);
		}
		if (request.getBookId() != null) {
			baseWhere.and(
				review.book.id.eq(request.getBookId())
			);
		}

		// 부분 일치 조건
		if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
			baseWhere.and(
				review.user.nickname.containsIgnoreCase(request.getKeyword())
					.or(review.content.containsIgnoreCase(request.getKeyword()))
					.or(review.book.title.containsIgnoreCase(request.getKeyword()))
			);
		}

		BooleanBuilder where = new BooleanBuilder(baseWhere);

		// 커서 조건
		if (request.getCursor() != null && request.getAfter() != null) {
			if ("rating".equals(request.getOrderBy())) {
				int rating = Integer.parseInt(request.getCursor());
				if ("ASC".equals(request.getDirection())) {
					where.and(
						review.rating.gt(rating)
							.or(review.rating.eq(rating)
								.and(review.createdAt.gt(request.getAfter())))
					);
				} else {
					where.and(
						review.rating.lt(rating)
							.or(review.rating.eq(rating)
								.and(review.createdAt.lt(request.getAfter())))
					);
				}
			} else { // default값: createdAt
				if ("ASC".equals(request.getDirection())) {
					where.and(
						review.createdAt.gt(request.getAfter())
					);
				} else {
					where.and(
						review.createdAt.lt(request.getAfter())
					);
				}
			}
		}

		// 정렬 기준
		boolean isRating = "rating".equals(request.getOrderBy());
		boolean isAsc = "ASC".equals(request.getDirection());

		OrderSpecifier<?> orderSpecifier = isRating
			? (isAsc ? review.rating.asc() : review.rating.desc())
			: (isAsc ? review.createdAt.asc() : review.createdAt.desc());

		List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
		orderSpecifiers.add(orderSpecifier);
		if (isRating) {
			orderSpecifiers.add(isAsc ? review.createdAt.asc() : review.createdAt.desc());
		}

		// 조회
		List<Review> reviews = queryFactory
			.selectFrom(review)
			.join(review.book, book).fetchJoin()
			.join(review.user, user).fetchJoin()
			.where(where)
			.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
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
			if ("rating".equals(request.getOrderBy())) {
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

	@Override
	public CursorPageResponse<Review> findLikedReviewsByRequest(LikedReviewSearchRequest request) {

		int limit = request.getLimit() > 0 ? request.getLimit() : 50;

		BooleanBuilder baseWhere = new BooleanBuilder();
		baseWhere.and(reviewLike.user.id.eq(request.getUserId()));

		BooleanBuilder where = new BooleanBuilder(baseWhere);

		// 커서 조건
		if (request.getCursor() != null && request.getAfter() != null) {
			where.and(reviewLike.createdAt.lt(request.getAfter()));
		}

		List<Review> reviews = queryFactory
			.select(reviewLike.review)
			.from(reviewLike)
			.join(reviewLike.review, review)
			.join(review.book, book).fetchJoin()
			.join(review.user, user).fetchJoin()
			.where(where)
			.orderBy(reviewLike.createdAt.desc())
			.limit(limit + 1)
			.fetch();

		boolean hasNext = reviews.size() > limit;
		if (hasNext) {
			reviews.remove(reviews.size() - 1);
		}

		Long totalElements = queryFactory
			.select(reviewLike.count())
			.from(reviewLike)
			.where(baseWhere)
			.fetchOne();

		String nextCursor = null;
		Instant nextAfter = null;
		if (hasNext) {
			Review lastReview = reviews.get(reviews.size() - 1);
			nextCursor = lastReview.getCreatedAt().toString();
			nextAfter = lastReview.getCreatedAt();
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

}
