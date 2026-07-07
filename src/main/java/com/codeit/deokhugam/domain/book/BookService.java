package com.codeit.deokhugam.domain.book;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.codeit.deokhugam.domain.book.dto.BookPatchRequest;
import com.codeit.deokhugam.domain.book.dto.BookPostRequest;
import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.book.dto.BookSearchUserRequest;
import com.codeit.deokhugam.domain.bookstatus.BookStatusType;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

public interface BookService {

	void validateIsbn(String isbn);

	BookResponse save(BookPostRequest req, String imgKey, String imgUrl, String category);

	CursorPageResponse<BookResponse> findAllByKeyword(BookSearchRequest req);

	Map<UUID, BookStatusType> getBookStatuses(List<UUID> bookIds, UUID userId);

	CursorPageResponse<BookResponse> findAllByUserId(BookSearchUserRequest req, UUID userId);

	BookResponse findById(UUID bookId, UUID userId);

	String getImageKey(UUID bookId);

	BookResponse update(UUID bookId, BookPatchRequest req, String url);

	void deleteSoft(UUID bookId);

	void delete(UUID bookId);
}
