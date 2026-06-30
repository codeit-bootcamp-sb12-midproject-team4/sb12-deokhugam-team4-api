package com.codeit.deokhugam.domain.book;

import java.util.UUID;

import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.book.dto.BookSearchUserRequest;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

public interface BookQueryRepository {

	CursorPageResponse<BookResponse> findAllByKeyword(BookSearchRequest req);

	CursorPageResponse<BookResponse> findAllByUserId(BookSearchUserRequest req, UUID userId);

}
