package com.codeit.deokhugam.domain.book.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RequestBody;
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
import com.codeit.deokhugam.domain.book.dto.CustomMultipartFile;
import com.codeit.deokhugam.domain.client.isbn.IsbnClient;
import com.codeit.deokhugam.domain.client.ocr.OcrClient;
import com.codeit.deokhugam.domain.common.CursorPageResponse;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

	@GetMapping("/search")
	public ResponseEntity<CursorPageResponse<BookResponse>> searchBooks(
			@ModelAttribute @Valid BookSearchRequest req
	) {
		CursorPageResponse<BookResponse> res = bookFacade.searchAllByKeyword(req);	// -> userId 필요
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



	@Getter
	@NoArgsConstructor
	public static class BatchRequest {
		private List<String> isbns;
	}
	@PostMapping("/batchload")
	public String runBatchLoad(@RequestBody BatchRequest req) throws InterruptedException {
		List<String> targetIsbns = req.getIsbns();

		if (targetIsbns == null || targetIsbns.isEmpty()) {
			return "ISBN 목록이 비어있습니다.";
		}

		int successCount = 0;
		log.info("-------------------------------- 🔥 총 {}건의 도서 데이터 적재 배치를 시작합니다...", targetIsbns.size());

		for (String isbn : targetIsbns) {
			try {
				try {
				 	bookService.validateIsbn(isbn);
				} catch (IllegalArgumentException e) {
					log.info("-------------------------------- ❌ ISBN: {} 중복으로 인한 도서 데이터 적재 실패", isbn);
					continue;
				}
				BookIsbnResponse isbnResponse = isbnClient.getInfoFromIsbn(isbn);
				if (isbnResponse != null) {
					BookPostRequest postRequest = BookPostRequest.builder()
							.title(isbnResponse.getTitle())
							.author(isbnResponse.getAuthor())
							.description(isbnResponse.getDescription())
							.publisher(isbnResponse.getPublisher())
							.publishedDate(isbnResponse.getPublishedDate())
							.isbn(isbnResponse.getIsbn())
							.build();

					String imageUrl = isbnResponse.getThumbnailImage();
					MultipartFile multipartFile = null;
					if (imageUrl != null && !imageUrl.isBlank()) {
						multipartFile = downloadImageAsMultipartFile(imageUrl, isbn + ".jpg");
					}

					bookFacade.post(postRequest, multipartFile);
					successCount++;
					log.info("-------------------------------- ✅ ISBN: {} 도서 데이터 적재 성공", isbn);
				}

				Thread.sleep(1500 + (long)(Math.random() * 1000));
			} catch (Exception e) {
				log.error("-------------------------------- ❌ ISBN: {} 도서 데이터 적재 실패", isbn, e);
			}
		}
		log.info("-------------------------------- ✅ 배치 적재 완료: {}건 성공, {}건 실패", successCount, targetIsbns.size() - successCount);
		return "배치 적재 완료";
	}
	private MultipartFile downloadImageAsMultipartFile(String imageUrl, String fileName) throws Exception {
		URL url = new URL(imageUrl);

		// 1. URL 스트림 열기
		try (InputStream is = url.openStream();
			 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			// 2. 바이트 단위로 읽어서 버퍼에 담기
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				baos.write(buffer, 0, bytesRead);
			}

			// 3. 다운로드 완료된 바이트 배열
			byte[] imageBytes = baos.toByteArray();

			// 4. 우리가 만든 Custom 객체로 포장해서 반환!
			return new CustomMultipartFile(
				imageBytes,
				"thumbnail", // 폼 데이터의 파라미터 이름
				fileName,    // 원본 파일명 (ex: 9788960773417.jpg)
				"image/jpeg" // 컨텐츠 타입
			);
		}
	}

}