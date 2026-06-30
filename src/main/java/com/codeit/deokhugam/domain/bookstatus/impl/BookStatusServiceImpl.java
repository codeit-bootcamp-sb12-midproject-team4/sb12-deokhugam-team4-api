package com.codeit.deokhugam.domain.bookstatus.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.book.BookRepository;
import com.codeit.deokhugam.domain.bookstatus.BookStatus;
import com.codeit.deokhugam.domain.bookstatus.BookStatusRepository;
import com.codeit.deokhugam.domain.bookstatus.BookStatusService;
import com.codeit.deokhugam.domain.bookstatus.BookStatusType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookStatusServiceImpl implements BookStatusService {
	private final BookStatusRepository bookStatusRepository;
	private final BookRepository bookRepository;

	@Override
	@Transactional
	public void putStatus(UUID bookId, UUID userId, BookStatusType type) {
		bookStatusRepository.findByBookIdAndUserId(bookId, userId)
			.ifPresentOrElse(
				existingStatus -> {
					existingStatus.updateStatus(type);
				},
				() -> {
					Book book = bookRepository.getReferenceById(bookId);
					//User user = userRepository.getReferenceById(userId);
					BookStatus newStatus = BookStatus.builder()
						.book(book)
						.user(null)
						.status(type)
						.build();
					bookStatusRepository.save(newStatus);
				}
			);
	}

	@Override
	@Transactional
	public void deleteStatus(UUID bookId, UUID userId) {
		bookStatusRepository.deleteByBookIdAndUserId(bookId, userId);
	}
}
