package com.codeit.deokhugam.domain.booksearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface BookElasticsearchRepository extends ElasticsearchRepository<BookDocument, String> {
}