package com.codeit.deokhugam.domain.booksearch.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
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

import java.util.List;
import java.util.stream.Collectors;

import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.booksearch.BookDocument;
import com.codeit.deokhugam.domain.booksearch.BookElasticsearchService;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

@Service
@RequiredArgsConstructor
public class BookElasticsearchServiceImpl implements BookElasticsearchService {
	private final ElasticsearchOperations elasticsearchOperations;
	private final ApplicationEventPublisher applicationEventPublisher;

	public List<String> getAutocompleteSuggestions(String prefix) {
		Suggester suggester = Suggester.of(s -> s
			.suggesters("title-suggester", f -> f
				.prefix(prefix) // 사용자가 입력 중인 앞글자 (ex: "스프")
				.completion(c -> c
					.field("titleSuggest") // Entity에 선언한 @CompletionField 필드명
					.size(5)               // 최대 5개 추천
					.skipDuplicates(true)  // 중복된 책 제목은 하나로 병합
				)
			)
		);

		NativeQuery query = NativeQuery.builder()
			.withSuggester(suggester)
			.build();

		SearchHits<BookDocument> searchHits = elasticsearchOperations.search(query, BookDocument.class);

		Suggest suggest = searchHits.getSuggest();

		if (suggest == null) {
			return List.of();
		}

		return suggest.getSuggestion("title-suggester").getEntries().stream()
			.flatMap(entry -> entry.getOptions().stream())
			.map(Suggest.Suggestion.Entry.Option::getText)
			.collect(Collectors.toList());
	}

	public CursorPageResponse<BookResponse> searchBooks(BookSearchRequest req) {

		NativeQueryBuilder builder = NativeQuery.builder()
			.withQuery(q -> q
				.bool(b -> b
					// 🕸️ [1] 넓은 그물망 (기본 검색 - 형태소 분석 대상에 isbn 추가)
					.must(must -> must
						.multiMatch(m -> m
							.fields("title^5", "author^3", "categoryPath^2", "publisher^2", "description", "isbn") // ⭐️ 여기에 isbn 추가!
							.query(req.getKeyword())
						)
					)
					// 🎯 [2] 핀셋 부스터 1 (완벽 일치)
					.should(should -> should
						.multiMatch(m -> m
							.fields("publisher.keyword^20", "author.keyword^20", "title.keyword^20")
							.query(req.getKeyword())
						)
					)
					// 🎯 [3] 핀셋 부스터 2 (구문 일치 - Phrase)
					.should(should -> should
						.multiMatch(m -> m
							.fields("title^10", "publisher^10", "categoryPath^10")
							.type(TextQueryType.Phrase)
							.query(req.getKeyword())
						)
					)
					// 🎯 [4] ISBN 전용 부스터 (검색어가 ISBN과 정확히 일치하면 검색 결과 최상단으로 멱살 잡고 올림)
					.should(should -> should
						.term(t -> t
							.field("isbn")
							.value(req.getKeyword())
							.boost(100.0f) // 100배 가중치
						)
					)
				)
			)
			.withPageable(PageRequest.of(0, req.getLimit() + 1));

		String orderBy = req.getOrderBy();

		if ("score".equals(orderBy)) {
			// [A] 정확도순: 검색어와 가장 잘 맞는 책이 위로 와야 하므로 무조건 내림차순(DESC)
			builder.withSort(Sort.by(Sort.Direction.DESC, "_score"));
		} else {
			// [B] 일반 정렬(출판일, 평점, 제목 등): 클라이언트가 요청한 방향(ASC/DESC) 적용
			Sort.Direction direction = Sort.Direction.fromString(req.getDirection());

			// 제목(title)은 형태소 분석이 아닌 원본 문자열(keyword) 기준으로 정렬해야 함
			String sortField = "title".equals(orderBy) ? "title.keyword" : orderBy;
			builder.withSort(Sort.by(direction, sortField));
		}

		builder.withSort(Sort.by(Sort.Direction.ASC, "id.keyword"));

		if (StringUtils.hasText(req.getCursor())) {
			String[] parts = req.getCursor().split("_", 2);
			Object sortValue = parseSortValue(req.getOrderBy(), parts[0]);
			String lastId = parts[1];

			// 빌더에 커서 조건 추가
			builder.withSearchAfter(List.of(sortValue, lastId));
		}

		Query query = builder.build();

		SearchHits<BookDocument> searchHits = elasticsearchOperations.search(query, BookDocument.class);
		List<SearchHit<BookDocument>> hits = searchHits.getSearchHits();

		// hasnext
		boolean hasNext = hits.size() > req.getLimit();
		List<SearchHit<BookDocument>> contentHits = hasNext ? hits.subList(0, req.getLimit()) : hits;

		List<BookResponse> content = contentHits.stream()
			.map(SearchHit::getContent)
			.map(BookResponse::from)
			.collect(Collectors.toList());

		// nextCursor
		String nextCursor = null;
		if (hasNext && !contentHits.isEmpty()) {
			SearchHit<BookDocument> lastHit = contentHits.get(contentHits.size() - 1);
			List<Object> sortValues = lastHit.getSortValues();
			nextCursor = sortValues.get(0).toString() + "_" + sortValues.get(1).toString();
		}

		return CursorPageResponse.<BookResponse>builder()
			.content(content)
			.nextCursor(nextCursor)
			.nextAfter(null) // Elasticsearch 정렬 기반 커서이므로 생략
			.size(req.getLimit())
			.totalElements(searchHits.getTotalHits()) // 전체 매칭 개수
			.hasNext(hasNext)
			.build();
	}
	private Object parseSortValue(String orderBy, String value) { // orderBy 타입에 맞추어 문자열을 파싱
		return switch (orderBy) {
			case "rating" -> Double.parseDouble(value);
			case "reviewCount" -> Long.parseLong(value);
			default -> value;
		};
	}
}
