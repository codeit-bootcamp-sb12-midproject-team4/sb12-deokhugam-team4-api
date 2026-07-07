package com.codeit.deokhugam.domain.book;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.codeit.deokhugam.domain.book.dto.BookPatchRequest;
import com.codeit.deokhugam.domain.book.dto.BookPostRequest;
import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.book.dto.BookSearchUserRequest;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

public interface BookFacade {

	BookResponse post(BookPostRequest req, MultipartFile img);

	CursorPageResponse<BookResponse> findAllByKeyword(BookSearchRequest req);

	CursorPageResponse<BookResponse> searchAllByKeyword(BookSearchRequest req);

	CursorPageResponse<BookResponse> findAllByUserId(BookSearchUserRequest req, UUID userId);

	BookResponse findById(UUID bookId, UUID userID);

	BookResponse patch(UUID bookId, BookPatchRequest req, MultipartFile img);

	void delete(UUID bookId);
}
