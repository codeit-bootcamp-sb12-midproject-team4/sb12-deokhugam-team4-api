package com.codeit.deokhugam.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.booksearch.BookElasticsearchService;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

@Configuration
@Profile("test")
public class TestElasticsearchConfig {

	@Bean
	public BookElasticsearchService bookElasticsearchService() {
		return new BookElasticsearchService() {
			@Override
			public List<String> getAutocompleteSuggestions(String prefix) {
				throw new UnsupportedOperationException("Elasticsearch is disabled in test profile");
			}

			@Override
			public CursorPageResponse<BookResponse> searchBooks(BookSearchRequest req) {
				throw new UnsupportedOperationException("Elasticsearch is disabled in test profile");
			}
		};
	}
}
