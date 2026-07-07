package com.codeit.deokhugam.domain.booksearch;

import java.util.List;

import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

public interface BookElasticsearchService {

	List<String> getAutocompleteSuggestions(String prefix);

	CursorPageResponse<BookResponse> searchBooks(BookSearchRequest req);

}
