package com.codeit.deokhugam.domain.book.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.book.BookMapper;
import com.codeit.deokhugam.domain.book.BookService;
import com.codeit.deokhugam.domain.book.dto.BookPatchRequest;
import com.codeit.deokhugam.domain.book.dto.BookPostRequest;
import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
	private final BookMapper bookMapper;

	@Override
	public BookResponse save(BookPostRequest req, String url, String category) {
		Book book = bookMapper.toBook(req, url, null);
		return null;
	}

	@Override
	public CursorPageResponse<BookResponse> findAllByKeyword(BookSearchRequest req) {
		return null;
	}

	@Override
	public CursorPageResponse<BookResponse> findAllByUserId(UUID userId) {
		return null;
	}

	@Override
	public BookResponse findById(UUID bookId) {
		return null;
	}

	@Override
	public BookResponse update(UUID bookId, BookPatchRequest req, String url) {
		return null;
	}

	@Override
	public void deleteSoft(UUID bookId) {

	}

	@Override
	public void delete(UUID bookId) {

	}

}
