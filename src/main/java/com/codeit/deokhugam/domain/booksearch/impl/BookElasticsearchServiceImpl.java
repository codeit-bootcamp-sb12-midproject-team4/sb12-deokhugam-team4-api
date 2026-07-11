package com.codeit.deokhugam.domain.booksearch.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import co.elastic.clients.elasticsearch.core.search.Suggester;
import lombok.RequiredArgsConstructor;

import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.booksearch.BookDocument;
import com.codeit.deokhugam.domain.booksearch.BookElasticsearchService;
import com.codeit.deokhugam.domain.booksearch.SearchKeywordService;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

@Service
@Profile({"dev", "dev-batch", "prod", "test-es"})
@RequiredArgsConstructor
public class BookElasticsearchServiceImpl implements BookElasticsearchService {

	private final ElasticsearchOperations elasticsearchOperations;
	private final ApplicationEventPublisher applicationEventPublisher;
	private final SearchKeywordService searchKeywordService;

	public List<String> getAutocompleteSuggestions(String prefix) {
		Suggester suggester = Suggester.of(s -> s
			.suggesters("title-suggester", f -> f
				.prefix(prefix)
				.completion(c -> c
					.field("titleSuggest")
					.size(5)
					.skipDuplicates(true)
				)
			)
		);

		NativeQuery query = NativeQuery.builder()
			.withSuggester(suggester)
			.build();

		SearchHits<BookDocument> searchHits =
			elasticsearchOperations.search(query, BookDocument.class);

		Suggest suggest = searchHits.getSuggest();

		if (suggest == null) {
			return List.of();
		}

		return suggest.getSuggestion("title-suggester")
			.getEntries()
			.stream()
			.flatMap(entry -> entry.getOptions().stream())
			.map(Suggest.Suggestion.Entry.Option::getText)
			.collect(Collectors.toList());
	}

	public CursorPageResponse<BookResponse> searchBooks(BookSearchRequest req) {

		// 검색어 로그 저장 (트렌딩 키워드용)
		if (StringUtils.hasText(req.getKeyword())) {
			searchKeywordService.saveKeyword(req.getKeyword().trim());
		}

		NativeQueryBuilder builder = NativeQuery.builder();

		if (req.getKeyword() == null || req.getKeyword().isBlank()) {

			builder.withQuery(q -> q.matchAll(m -> m));

		} else {

			builder.withQuery(q -> q
				.bool(b -> b
					.must(must -> must
						.multiMatch(m -> m
							.fields(
								"title^5",
								"author^3",
								"categoryPath^2",
								"publisher^2",
								"description",
								"isbn"
							)
							.query(req.getKeyword())
						)
					)
					.should(should -> should
						.term(t -> t
							.field("isbn")
							.value(req.getKeyword())
							.boost(500.0f)
						)
					)
					.should(should -> should
						.term(t -> t
							.field("title.keyword")
							.value(req.getKeyword())
							.boost(200.0f)
						)
					)
					.should(should -> should
						.term(t -> t
							.field("author.keyword")
							.value(req.getKeyword())
							.boost(400.0f)
						)
					)
					.should(should -> should
						.matchPhrase(m -> m
							.field("title")
							.query(req.getKeyword())
							.boost(100.0f)
						)
					)
				)
			);
		}

		builder.withPageable(PageRequest.of(0, req.getLimit() + 1));

		String orderBy = req.getOrderBy();

		if ("score".equals(orderBy)) {
			builder.withSort(Sort.by(Sort.Direction.DESC, "_score"));
		} else {
			Sort.Direction direction = Sort.Direction.fromString(req.getDirection());
			String sortField = "title".equals(orderBy) ? "title.keyword" : orderBy;
			builder.withSort(Sort.by(direction, sortField));
		}

		builder.withSort(Sort.by(Sort.Direction.ASC, "id.keyword"));

		if (StringUtils.hasText(req.getCursor())) {
			String[] parts = req.getCursor().split("_", 2);
			Object sortValue = parseSortValue(req.getOrderBy(), parts[0]);
			String lastId = parts[1];

			builder.withSearchAfter(List.of(sortValue, lastId));
		}

		Query query = builder.build();

		SearchHits<BookDocument> searchHits =
			elasticsearchOperations.search(query, BookDocument.class);

		List<SearchHit<BookDocument>> hits = searchHits.getSearchHits();

		boolean hasNext = hits.size() > req.getLimit();
		List<SearchHit<BookDocument>> contentHits =
			hasNext ? hits.subList(0, req.getLimit()) : hits;

		List<BookResponse> content = contentHits.stream()
			.map(SearchHit::getContent)
			.map(BookResponse::from)
			.collect(Collectors.toList());

		String nextCursor = null;

		if (hasNext && !contentHits.isEmpty()) {
			SearchHit<BookDocument> lastHit = contentHits.get(contentHits.size() - 1);
			List<Object> sortValues = lastHit.getSortValues();
			nextCursor = sortValues.get(0) + "_" + sortValues.get(1);
		}

		return CursorPageResponse.<BookResponse>builder()
			.content(content)
			.nextCursor(nextCursor)
			.nextAfter(null)
			.size(req.getLimit())
			.totalElements(searchHits.getTotalHits())
			.hasNext(hasNext)
			.build();
	}

	private Object parseSortValue(String orderBy, String value) {
		return switch (orderBy) {
			case "rating" -> Double.parseDouble(value);
			case "reviewCount" -> Long.parseLong(value);
			default -> value;
		};
	}
}