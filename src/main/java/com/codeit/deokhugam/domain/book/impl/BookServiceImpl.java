package com.codeit.deokhugam.domain.book.impl;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.book.BookCategory;
import com.codeit.deokhugam.domain.book.BookCategoryRepository;
import com.codeit.deokhugam.domain.book.BookMapper;
import com.codeit.deokhugam.domain.book.BookRepository;
import com.codeit.deokhugam.domain.book.BookService;
import com.codeit.deokhugam.domain.book.dto.BookPatchRequest;
import com.codeit.deokhugam.domain.book.dto.BookPostRequest;
import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.book.dto.BookSearchUserRequest;
import com.codeit.deokhugam.domain.bookstatus.BookStatus;
import com.codeit.deokhugam.domain.bookstatus.BookStatusRepository;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
	private final BookMapper bookMapper;
	private final BookCategoryRepository bookCategoryRepository;
	private final BookRepository bookRepository;
	private final BookStatusRepository bookStatusRepository;

	@Override
	@Transactional
	public BookResponse save(BookPostRequest req, String url, String category) {
		if (bookRepository.existsByIsbn(req.getIsbn())) {
			throw new IllegalArgumentException("이미 등록된 도서입니다. (ISBN중복 : " + req.getIsbn() + ")");
		}
		BookCategory bookCategory = null;
		if (!category.isBlank()) {
			bookCategory = findBookCategory(category);
		}
		Book book = bookRepository.save(bookMapper.toBook(req, url, bookCategory));
		return bookMapper.toResponse(book, null);
	}
	private BookCategory findBookCategory(String fullPath) {
		return bookCategoryRepository.findByPath(fullPath)
			.orElseGet(() -> saveNewCategories(fullPath));
	}
	private BookCategory saveNewCategories(String fullPath) {
		String[] categoryNames = fullPath.split(">");
		BookCategory currentParent = null;
		StringBuilder currentPathBuilder = new StringBuilder();

		for (String name : categoryNames) {
			if (name.isBlank()) continue;
			if (!currentPathBuilder.isEmpty()) {
				currentPathBuilder.append(">");
			}
			currentPathBuilder.append(name);
			String currentPath = currentPathBuilder.toString();

			final BookCategory finalParent = currentParent;
			currentParent = bookCategoryRepository.findByNameAndParent(name, currentParent)
				.orElseGet(() -> bookCategoryRepository.save(
					BookCategory.builder()
						.name(name)
						.path(currentPath)
						.parent(finalParent)
						.build()
				));
		}

		return currentParent;
	}

	@Override
	@Transactional(readOnly = true)
	public CursorPageResponse<BookResponse> findAllByKeyword(BookSearchRequest req) {
		return bookRepository.findAllByKeyword(req);
	}

	@Override
	@Transactional(readOnly = true)
	public CursorPageResponse<BookResponse> findAllByUserId(BookSearchUserRequest req, UUID userId) {
		return bookRepository.findAllByUserId(req, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public BookResponse findByIdWithStatus(UUID bookId, UUID userId) {
		return bookRepository.findByIdWithStatus(bookId, userId)
			.orElseThrow(() -> new NoSuchElementException("해당하는 도서 정보가 없습니다. (bookId : " + bookId + ")"));
	}

	@Override
	@Transactional
	public BookResponse update(UUID bookId, BookPatchRequest req, String url) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new NoSuchElementException("해당하는 도서 정보가 없습니다. (bookId : " + bookId + ")"));
		book.update(req.getTitle(), req.getAuthor(), req.getDescription(), req.getPublisher(), req.getPublishedDate());
		BookStatus bookStatus = bookStatusRepository.findByBookIdAndUserId(bookId, req.getUserId())
			.orElse(null);
		return bookMapper.toResponse(bookRepository.save(book), bookStatus);
	}

	@Override
	@Transactional
	public void deleteSoft(UUID bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new NoSuchElementException("해당하는 도서 정보가 없습니다. (bookId : " + bookId + ")"));
		book.markDeleted();
	}

	@Override
	@Transactional
	public void delete(UUID bookId) {
		bookRepository.deleteById(bookId);
	}

}
