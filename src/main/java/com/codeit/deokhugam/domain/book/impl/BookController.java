package com.codeit.deokhugam.domain.book.impl;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codeit.deokhugam.domain.book.BookFacade;
import com.codeit.deokhugam.domain.book.BookService;
import com.codeit.deokhugam.domain.book.dto.BookIsbnResponse;
import com.codeit.deokhugam.domain.book.dto.BookPatchRequest;
import com.codeit.deokhugam.domain.book.dto.BookPostRequest;
import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.book.dto.BookSearchUserRequest;
import com.codeit.deokhugam.domain.client.isbn.IsbnClient;
import com.codeit.deokhugam.domain.client.ocr.OcrClient;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {
	private final BookFacade bookFacade;
	private final BookService bookService;
	private final OcrClient ocrClient;
	private final IsbnClient isbnClient;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BookResponse> postBook(
			@RequestPart(value = "bookData") @Valid BookPostRequest req,
			@RequestPart(value = "thumbnailImage", required = false) MultipartFile img
	) {
		BookResponse res = bookFacade.post(req, img);
		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	@PostMapping(value = "/isbn/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> postIsbn(
			@RequestPart(value = "image") MultipartFile img
	) {
		String res = ocrClient.getIsbnFromImage(img);
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@GetMapping(value = "/info")
	public ResponseEntity<BookIsbnResponse> getBookInfo(
			@RequestParam(value = "isbn") @NotBlank
			@Pattern(regexp = "^(978|979)-?[0-9]{1,7}-?[0-9]{1,7}-?[0-9]{1,7}-?[0-9X0-9]$", message = "올바른 ISBN 형식이 아닙니다.")
			String isbn
	) {
		BookIsbnResponse res = isbnClient.getInfoFromIsbn(isbn);
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@GetMapping
	public ResponseEntity<CursorPageResponse<BookResponse>> getBooks(
			@ModelAttribute @Valid BookSearchRequest req
	) {
		CursorPageResponse<BookResponse> res = bookFacade.findAllByKeyword(req);	// -> userId 필요
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@GetMapping("/users/{userId}")
	public ResponseEntity<CursorPageResponse<BookResponse>> getUserBooks(
			@PathVariable UUID userId,
			@ModelAttribute @Valid BookSearchUserRequest req
	) {
		CursorPageResponse<BookResponse> res = bookFacade.findAllByUserId(req, userId);
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@GetMapping("/{bookId}")
	public ResponseEntity<BookResponse> getBookDetails(
			@PathVariable UUID bookId,
			@RequestParam @Nullable UUID userId
	) {
		BookResponse res = bookFacade.findById(bookId, userId);
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@PatchMapping("/{bookId}")
	public ResponseEntity<BookResponse> patchBook(
			@PathVariable UUID bookId,
			@RequestPart(value = "bookData") @Valid BookPatchRequest req,
			@RequestPart(value = "thumbnailImage", required = false) MultipartFile img
	) {
		BookResponse res = bookFacade.patch(bookId, req, img);
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@DeleteMapping("/{bookId}")
	public ResponseEntity<Void> logicalDeleteBook(@PathVariable UUID bookId) {
		bookService.deleteSoft(bookId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}

	@DeleteMapping("/{bookId}/hard")
	public ResponseEntity<Void> physicalDeleteBook(@PathVariable UUID bookId) {
		bookFacade.delete(bookId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}

}