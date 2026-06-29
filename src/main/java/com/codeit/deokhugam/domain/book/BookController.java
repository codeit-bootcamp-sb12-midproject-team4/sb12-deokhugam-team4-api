package com.codeit.deokhugam.domain.book;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import com.codeit.deokhugam.domain.book.dto.BookIsbnResponse;
import com.codeit.deokhugam.domain.book.dto.BookPatchRequest;
import com.codeit.deokhugam.domain.book.dto.BookPostRequest;
import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.book.dto.BookSearchRequest;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BookResponse> postBook(
			@RequestPart(value = "bookData") @Valid BookPostRequest req,
			@RequestPart(value = "thumbnailImage", required = false)MultipartFile img
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}

	@PostMapping(value = "/isbn/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> postIsbn(
			@RequestPart(value = "image") MultipartFile img
	) {
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@GetMapping(value = "/info")
	public ResponseEntity<BookIsbnResponse> getBookInfo(
			@RequestParam(value = "isbn") @NotBlank
			@Pattern(regexp = "^(978|979)-?[0-9]{1,7}-?[0-9]{1,7}-?[0-9]{1,7}-?[0-9X0-9]$", message = "올바른 ISBN 형식이 아닙니다.")
			String isbn
	) {
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@GetMapping
	public ResponseEntity<CursorPageResponse<BookResponse>> getBooks(
			@ModelAttribute @Valid BookSearchRequest req
	) {
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<CursorPageResponse<BookResponse>> getUserBooks(@PathVariable UUID userId) {
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@GetMapping("/{bookId}")
	public ResponseEntity<BookResponse> getBookDetails(@PathVariable UUID bookId) {
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@PatchMapping("/{bookId}")
	public ResponseEntity<BookResponse> patchBook(
			@PathVariable UUID bookId,
			@RequestPart(value = "bookData") @Valid BookPatchRequest req,
			@RequestPart(value = "thumbnailImage", required = false) MultipartFile img
	) {
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@DeleteMapping("/{bookId}")
	public ResponseEntity<Void> logicalDeleteBook(@PathVariable UUID bookId) {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}

	@DeleteMapping("/{bookId}")
	public ResponseEntity<Void> physicalDeleteBook(@PathVariable UUID bookId) {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}



}