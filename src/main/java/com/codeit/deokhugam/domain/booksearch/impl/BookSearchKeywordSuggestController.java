package com.codeit.deokhugam.domain.booksearch.impl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codeit.deokhugam.domain.booksearch.BookElasticsearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookSearchKeywordSuggestController {
	private final BookElasticsearchService service;

	@GetMapping("/keyword-suggest")
	public ResponseEntity<List<String>> getSuggestions(@RequestParam("query") String query) {
		if (query == null || query.trim().isEmpty()) {
			return ResponseEntity.ok(List.of());
		}
		List<String> res = service.getAutocompleteSuggestions(query);
		return ResponseEntity.ok(res);
	}
}
