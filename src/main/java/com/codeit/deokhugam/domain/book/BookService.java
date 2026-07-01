package com.codeit.deokhugam.domain.book;

import java.util.UUID;

import com.codeit.deokhugam.domain.book.dto.BookPatchRequest;
import com.codeit.deokhugam.domain.book.dto.BookPostRequest;
import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.book.dto.BookSearchUserRequest;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

public interface BookService {

	BookResponse save(BookPostRequest req, String url, String category);

	CursorPageResponse<BookResponse> findAllByKeyword(BookSearchRequest req);

	CursorPageResponse<BookResponse> findAllByUserId(BookSearchUserRequest req, UUID userId);

	BookResponse findById(UUID bookId, UUID userId);

	String getImageUrl(UUID bookId);

	BookResponse update(UUID bookId, BookPatchRequest req, String url);

	void deleteSoft(UUID bookId);

	void delete(UUID bookId);
}
