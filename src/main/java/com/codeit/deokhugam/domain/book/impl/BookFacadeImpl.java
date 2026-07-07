package com.codeit.deokhugam.domain.book.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.codeit.deokhugam.domain.book.BookFacade;
import com.codeit.deokhugam.domain.book.BookService;
import com.codeit.deokhugam.domain.book.dto.BookPatchRequest;
import com.codeit.deokhugam.domain.book.dto.BookPostRequest;
import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.book.dto.BookSearchUserRequest;
import com.codeit.deokhugam.domain.booksearch.BookElasticsearchService;
import com.codeit.deokhugam.domain.booksearch.event.BookSyncEvent;
import com.codeit.deokhugam.domain.booksearch.impl.BookElasticsearchServiceImpl;
import com.codeit.deokhugam.domain.bookstatus.BookStatusType;
import com.codeit.deokhugam.domain.client.category.CategoryClient;
import com.codeit.deokhugam.domain.client.s3.FileStorageClient;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookFacadeImpl implements BookFacade {
	private final BookService bookService;
	private final CategoryClient categoryClient;
	private final FileStorageClient fileStorageClient;
	private final BookElasticsearchService bookElasticsearchServiceImpl;

	@Override
	public BookResponse post(BookPostRequest req, MultipartFile img) {
		String imgUrl = null;
		String imgKey = null;
		String category = null;

		bookService.validateIsbn(req.getIsbn());

		if (img != null && !img.isEmpty()) {
			imgKey = fileStorageClient.uploadImage(img);
			imgUrl = fileStorageClient.getAttachFileUrl(imgKey);
		}

		if (req.getIsbn() != null && !req.getIsbn().isBlank()) {
			category = categoryClient.getCategoryFromIsbn(req.getIsbn());
		}

		return bookService.save(req, imgKey, imgUrl, category);
	}

	@Override
	public CursorPageResponse<BookResponse> findAllByKeyword(BookSearchRequest req) {
		CursorPageResponse<BookResponse> res = bookService.findAllByKeyword(req);
		res.getContent().forEach(book -> {
			if (book.getThumbnailUrl() != null) {
				String imgUrl = fileStorageClient.getAttachFileUrl(book.getThumbnailUrl());
				book.setThumbnailUrl(imgUrl);
			}
		});
		return res;
	}

	@Override
	public CursorPageResponse<BookResponse> searchAllByKeyword(BookSearchRequest req) {
		CursorPageResponse<BookResponse> res = bookElasticsearchServiceImpl.searchBooks(req);

		List<BookResponse> books = res.getContent();
		if (CollectionUtils.isEmpty(books)) {
			return res;
		}
		List<UUID> bookIds = books.stream()
			.map(BookResponse::getId)
			.collect(Collectors.toList());

		if (req.getUserId() != null) {
			Map<UUID, BookStatusType> statusMap = bookService.getBookStatuses(bookIds, req.getUserId());

			books.forEach(book -> {;
				BookStatusType status = statusMap.get(book.getId());
				book.setStatus(status);
			});
		}

		books.forEach(book -> {
			if (book.getThumbnailUrl() != null) {
				String imgUrl = fileStorageClient.getAttachFileUrl(book.getThumbnailUrl());
				book.setThumbnailUrl(imgUrl);
			}
		});

		return res;
	}

	@Override
	public CursorPageResponse<BookResponse> findAllByUserId(BookSearchUserRequest req, UUID userId) {
		CursorPageResponse<BookResponse> res = bookService.findAllByUserId(req, userId);
		res.getContent().forEach(book -> {
			if (book.getThumbnailUrl() != null) {
				String imgUrl = fileStorageClient.getAttachFileUrl(book.getThumbnailUrl());
				book.setThumbnailUrl(imgUrl);
			}
		});
		return res;
	}

	@Override
	public BookResponse findById(UUID bookId, UUID userId) {
		BookResponse res = bookService.findById(bookId, userId);
		if (res.getThumbnailUrl() != null) {
			String imgUrl = fileStorageClient.getAttachFileUrl(res.getThumbnailUrl());
			res.setThumbnailUrl(imgUrl);
		}
		return res;
	}

	@Override
	public BookResponse patch(UUID bookId, BookPatchRequest req, MultipartFile img) {
		String newKey = null;
		String newUrl = null;
		String oldKey = null;
		BookResponse res = null;

		if (img != null && !img.isEmpty()) {
			oldKey = bookService.getImageKey(bookId);
			newKey = fileStorageClient.uploadImage(img);
			newUrl = fileStorageClient.getAttachFileUrl(newKey);
		}

		try {
			res = bookService.update(bookId, req, newKey);
			if (oldKey != null) {
				fileStorageClient.deleteImage(oldKey);
				log.info("AWS S3 이미지 삭제 완료 (key : {})", oldKey);
			}
			if (res.getThumbnailUrl() != null) {
				res.setThumbnailUrl(newUrl);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (newKey != null) {
				fileStorageClient.deleteImage(newKey);
				log.info("AWS S3 새로운 이미지 삭제 완료 for Rollback (key : {})", newKey);
			}
		}
		return res;
	}

	@Override
	public void delete(UUID bookId) {
		String imgKey = bookService.getImageKey(bookId);
		bookService.delete(bookId);
		if (imgKey != null) {
			fileStorageClient.deleteImage(imgKey);
			log.info("AWS S3 이미지 삭제 완료 (key : {})", imgKey);
		}
	}

}
