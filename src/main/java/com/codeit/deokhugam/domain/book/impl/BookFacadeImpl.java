package com.codeit.deokhugam.domain.book.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codeit.deokhugam.domain.book.BookFacade;
import com.codeit.deokhugam.domain.book.BookRepository;
import com.codeit.deokhugam.domain.book.BookService;
import com.codeit.deokhugam.domain.book.dto.BookPatchRequest;
import com.codeit.deokhugam.domain.book.dto.BookPostRequest;
import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.client.category.CategoryClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookFacadeImpl implements BookFacade {
	private final BookService bookService;
	private final CategoryClient categoryClient;
	private final BookRepository bookRepository;

	@Override
	public BookResponse post(BookPostRequest req, MultipartFile img) {
		String url = null;
		if (img != null && !img.isEmpty()) {
			// AWS S3 저장 & URL 추출
		}

		String category = null;
		if (req.getIsbn() != null && !req.getIsbn().isBlank()) {
			category = categoryClient.getCategoryFromIsbn(req.getIsbn());
		}

		return bookService.save(req, url, category);
	}

	@Override
	public BookResponse patch(UUID bookId, BookPatchRequest req, MultipartFile img) {
		String newUrl = null;
		String oldUrl = null;
		BookResponse res = null;

		if (img != null && !img.isEmpty()) {
			oldUrl = bookService.getImageUrl(bookId);
			// AWS S3 새로운 이미지 저장 & URL 추출
		}

		try {
			res = bookService.update(bookId, req, newUrl);
			if (oldUrl != null) {
				// AWS S3 기존 이미지 삭제
			}
		} catch (Exception e) {
			// AWS S3 새로운 이미지 삭제
		}
		return res;
	}

}
