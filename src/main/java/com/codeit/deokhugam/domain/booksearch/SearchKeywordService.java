package com.codeit.deokhugam.domain.booksearch;

import java.time.Instant;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchKeywordService {

	private final SearchKeywordRepository searchKeywordRepository;

	@Transactional
	public void saveKeyword(String keyword) {

		if (keyword == null || keyword.isBlank()) {
			return;
		}

		SearchKeywordDocument document = SearchKeywordDocument.builder()
			.id(UUID.randomUUID().toString())
			.keyword(keyword.trim())
			.searchedAt(Instant.now())
			.build();

		searchKeywordRepository.save(document);
	}
}