package com.codeit.deokhugam.domain.booksearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

//검색 키워드 로그 Elasticsearch Repository
public interface SearchKeywordRepository
	extends ElasticsearchRepository<SearchKeywordDocument, String> {
}