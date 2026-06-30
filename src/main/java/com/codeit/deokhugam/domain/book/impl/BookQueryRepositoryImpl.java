package com.codeit.deokhugam.domain.book.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.codeit.deokhugam.domain.book.QBook;
import com.codeit.deokhugam.domain.book.QBookCategory;
import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.book.dto.BookSearchUserRequest;
import com.codeit.deokhugam.domain.book.BookQueryRepository;
import com.codeit.deokhugam.domain.bookstatus.QBookStatus;
import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BookQueryRepositoryImpl implements BookQueryRepository {
	private static final QBook b = QBook.book;
	private static final QBookCategory bc = QBookCategory.bookCategory;
	private static final QBookStatus bs = QBookStatus.bookStatus;

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorPageResponse<BookResponse> findAllByKeyword(BookSearchRequest req) {

		BooleanBuilder searchCondition = new BooleanBuilder();
		searchCondition.and(b.deletedAt.isNull());
		BooleanBuilder keywordWhere = new BooleanBuilder();
		keywordWhere.or(b.title.containsIgnoreCase(req.getKeyword()));
		keywordWhere.or(b.author.containsIgnoreCase(req.getKeyword()));
		keywordWhere.or(b.isbn.eq(req.getKeyword()));
		keywordWhere.or(b.bookCategory.path.containsIgnoreCase(req.getKeyword()));
		searchCondition.and(keywordWhere);

		Long totalElements = queryFactory
			.select(b.count())
			.from(b)
			.where(searchCondition)
			.fetchOne();
		long total = totalElements != null ? totalElements : 0L;
		if (total == 0) {
			return CursorPageResponse.<BookResponse>builder()
				.content(Collections.emptyList())
				.nextCursor(null)
				.nextAfter(null)
				.size(0)
				.totalElements(0)
				.hasNext(false)
				.build();
		}


		BooleanBuilder where = new BooleanBuilder(searchCondition);
		Order order = "DESC".equalsIgnoreCase(req.getDirection()) ? Order.DESC : Order.ASC;
		OrderSpecifier<?> orderSpecifier;
		switch (req.getOrderBy()) {
			case "title"         -> orderSpecifier = new OrderSpecifier<>(order, b.title);
			case "publishedDate" -> orderSpecifier = new OrderSpecifier<>(order, b.publishedDate);
			case "rating"        -> orderSpecifier = new OrderSpecifier<>(order, b.rating);
			case "reviewCount"   -> orderSpecifier = new OrderSpecifier<>(order, b.reviewCount);
			default              -> orderSpecifier = new OrderSpecifier<>(order, b.createdAt);
		}

		if (req.getCursor() != null && req.getAfter() != null) {
			try {
				Instant afterTime = Instant.parse(req.getAfter());
				BooleanBuilder cursorCondition = new BooleanBuilder();

				switch (req.getOrderBy()) {
					case "rating" -> {
						Double cursorVal = Double.parseDouble(req.getCursor());
						if (order == Order.DESC) {
							cursorCondition.or(b.rating.lt(cursorVal))
								.or(b.rating.eq(cursorVal).and(b.createdAt.lt(afterTime)));
						} else {
							cursorCondition.or(b.rating.gt(cursorVal))
								.or(b.rating.eq(cursorVal).and(b.createdAt.gt(afterTime)));
						}
					}
					case "reviewCount" -> {
						Long cursorVal = Long.parseLong(req.getCursor());
						if (order == Order.DESC) {
							cursorCondition.or(b.reviewCount.lt(cursorVal))
								.or(b.reviewCount.eq(cursorVal).and(b.createdAt.lt(afterTime)));
						} else {
							cursorCondition.or(b.reviewCount.gt(cursorVal))
								.or(b.reviewCount.eq(cursorVal).and(b.createdAt.gt(afterTime)));
						}
					}
					case "publishedDate" -> {
						LocalDate cursorVal = LocalDate.parse(req.getCursor());
						if (order == Order.DESC) {
							cursorCondition.or(b.publishedDate.lt(cursorVal))
								.or(b.publishedDate.eq(cursorVal).and(b.createdAt.lt(afterTime)));
						} else {
							cursorCondition.or(b.publishedDate.gt(cursorVal))
								.or(b.publishedDate.eq(cursorVal).and(b.createdAt.gt(afterTime)));
						}
					}
					case "title" -> {
						String cursorVal = req.getCursor();
						if (order == Order.DESC) {
							cursorCondition.or(b.title.lt(cursorVal))
								.or(b.title.eq(cursorVal).and(b.createdAt.lt(afterTime)));
						} else {
							cursorCondition.or(b.title.gt(cursorVal))
								.or(b.title.eq(cursorVal).and(b.createdAt.gt(afterTime)));
						}
					}
					default -> { // createdAt 기본 정렬
						LocalDateTime cursorVal = LocalDateTime.parse(req.getCursor());
						if (order == Order.DESC) {
							cursorCondition.or(b.createdAt.lt(Instant.from(cursorVal)));
						} else {
							cursorCondition.or(b.createdAt.gt(Instant.from(cursorVal)));
						}
					}
				}
				where.and(cursorCondition);
			} catch (Exception ignored) {}
		}

		int limit = req.getLimit();
		List<BookResponse> content = queryFactory
			.select(Projections.fields(BookResponse.class,
				b.id,
				b.title,
				b.author,
				b.description,
				b.publisher,
				b.publishedDate,
				b.isbn,
				b.thumbnailUrl,
				b.reviewCount,
				b.rating,
				bc.path.as("categoryPath"),
				bs.status.as("status"),
				b.createdAt,
				b.updatedAt
			))
			.from(b)
			.leftJoin(b.bookCategory, bc)
			.leftJoin(bs).on(bs.book.id.eq(b.id).and(bs.user.id.eq(req.getUserId())))
			.where(where)
			.orderBy(
				orderSpecifier,
				new OrderSpecifier<>(order, b.createdAt)
			)
			.limit(limit + 1)
			.fetch();

		boolean hasNext = false;
		String nextCursor = null;
		Instant nextAfter = null;
		if (content.size() > limit) {
			hasNext = true;
			content.remove(limit);
		}
		if (!content.isEmpty() && hasNext) {
			BookResponse lastItem = content.get(content.size() - 1);
			nextAfter = lastItem.getCreatedAt();
			nextCursor = switch (req.getOrderBy()) {
				case "title"         -> lastItem.getTitle();
				case "publishedDate" -> lastItem.getPublishedDate().toString();
				case "rating"        -> String.valueOf(lastItem.getRating());
				case "reviewCount"   -> String.valueOf(lastItem.getReviewCount());
				default              -> lastItem.getCreatedAt().toString();
			};
		}

		return CursorPageResponse.<BookResponse>builder()
			.content(content)
			.nextCursor(nextCursor)
			.nextAfter(nextAfter)
			.size(content.size())
			.totalElements(total)
			.hasNext(hasNext)
			.build();
	}

	@Override
	public CursorPageResponse<BookResponse> findAllByUserId(BookSearchUserRequest req, UUID userId) {
		BooleanBuilder baseCondition = new BooleanBuilder();
		baseCondition.and(b.deletedAt.isNull());
		baseCondition.and(bs.user.id.eq(userId));

		Long totalElements = queryFactory
			.select(b.count())
			.from(b)
			.innerJoin(bs).on(bs.book.id.eq(b.id))
			.where(baseCondition)
			.fetchOne();
		long total = totalElements != null ? totalElements : 0L;
		if (total == 0) {
			return CursorPageResponse.<BookResponse>builder()
				.content(Collections.emptyList())
				.nextCursor(null)
				.nextAfter(null)
				.size(0)
				.totalElements(0)
				.hasNext(false)
				.build();
		}

		BooleanBuilder where = new BooleanBuilder(baseCondition);
		Order order = "DESC".equalsIgnoreCase(req.getDirection()) ? Order.DESC : Order.ASC;
		OrderSpecifier<?> orderSpecifier = switch (req.getOrderBy()) {
			case "title"         -> new OrderSpecifier<>(order, b.title);
			case "publishedDate" -> new OrderSpecifier<>(order, b.publishedDate);
			case "rating"        -> new OrderSpecifier<>(order, b.rating);
			case "reviewCount"   -> new OrderSpecifier<>(order, b.reviewCount);
			default              -> new OrderSpecifier<>(order, b.createdAt);
		};

		if (req.getCursor() != null && req.getAfter() != null) {
			try {
				Instant afterTime = Instant.parse(req.getAfter());
				BooleanBuilder cursorCondition = new BooleanBuilder();

				switch (req.getOrderBy()) {
					case "rating" -> {
						Double cursorVal = Double.parseDouble(req.getCursor());
						if (order == Order.DESC) {
							cursorCondition.or(b.rating.lt(cursorVal))
								.or(b.rating.eq(cursorVal).and(b.createdAt.lt(afterTime)));
						} else {
							cursorCondition.or(b.rating.gt(cursorVal))
								.or(b.rating.eq(cursorVal).and(b.createdAt.gt(afterTime)));
						}
					}
					case "reviewCount" -> {
						Long cursorVal = Long.parseLong(req.getCursor());
						if (order == Order.DESC) {
							cursorCondition.or(b.reviewCount.lt(cursorVal))
								.or(b.reviewCount.eq(cursorVal).and(b.createdAt.lt(afterTime)));
						} else {
							cursorCondition.or(b.reviewCount.gt(cursorVal))
								.or(b.reviewCount.eq(cursorVal).and(b.createdAt.gt(afterTime)));
						}
					}
					case "publishedDate" -> {
						LocalDate cursorVal = LocalDate.parse(req.getCursor());
						if (order == Order.DESC) {
							cursorCondition.or(b.publishedDate.lt(cursorVal))
								.or(b.publishedDate.eq(cursorVal).and(b.createdAt.lt(afterTime)));
						} else {
							cursorCondition.or(b.publishedDate.gt(cursorVal))
								.or(b.publishedDate.eq(cursorVal).and(b.createdAt.gt(afterTime)));
						}
					}
					case "title" -> {
						String cursorVal = req.getCursor();
						if (order == Order.DESC) {
							cursorCondition.or(b.title.lt(cursorVal))
								.or(b.title.eq(cursorVal).and(b.createdAt.lt(afterTime)));
						} else {
							cursorCondition.or(b.title.gt(cursorVal))
								.or(b.title.eq(cursorVal).and(b.createdAt.gt(afterTime)));
						}
					}
					default -> {
						Instant cursorVal = Instant.parse(req.getCursor());
						if (order == Order.DESC) {
							cursorCondition.or(b.createdAt.lt(cursorVal));
						} else {
							cursorCondition.or(b.createdAt.gt(cursorVal));
						}
					}
				}
				where.and(cursorCondition);
			} catch (Exception ignored) {}
		}

		int limit = req.getLimit();
		List<BookResponse> content = queryFactory
			.select(Projections.fields(BookResponse.class,
				b.id,
				b.title,
				b.author,
				b.description,
				b.publisher,
				b.publishedDate,
				b.isbn,
				b.thumbnailUrl,
				b.reviewCount,
				b.rating,
				bc.path.as("categoryPath"),
				bs.status.as("status"),
				b.createdAt,
				b.updatedAt
			))
			.from(b)
			.leftJoin(b.bookCategory, bc)
			.innerJoin(bs).on(bs.book.id.eq(b.id))
			.where(where)
			.orderBy(
				orderSpecifier,
				new OrderSpecifier<>(order, b.createdAt)
			)
			.limit(limit + 1)
			.fetch();

		boolean hasNext = false;
		String nextCursor = null;
		Instant nextAfter = null;

		if (content.size() > limit) {
			hasNext = true;
			content.remove(limit);
		}

		if (!content.isEmpty() && hasNext) {
			BookResponse lastItem = content.get(content.size() - 1);
			nextAfter = lastItem.getCreatedAt();

			nextCursor = switch (req.getOrderBy()) {
				case "title"         -> lastItem.getTitle();
				case "publishedDate" -> lastItem.getPublishedDate().toString();
				case "rating"        -> String.valueOf(lastItem.getRating());
				case "reviewCount"   -> String.valueOf(lastItem.getReviewCount());
				default              -> lastItem.getCreatedAt().toString();
			};
		}

		return CursorPageResponse.<BookResponse>builder()
			.content(content)
			.nextCursor(nextCursor)
			.nextAfter(nextAfter)
			.size(content.size())
			.totalElements(total)
			.hasNext(hasNext)
			.build();
	}

}
