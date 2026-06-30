package com.codeit.deokhugam.domain.bookstatus.impl;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codeit.deokhugam.domain.bookstatus.BookStatusService;
import com.codeit.deokhugam.domain.bookstatus.BookStatusType;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-status")
public class BookStatusController {
	private final BookStatusService bookStatusService;

	@PutMapping("/{bookId}")
	public ResponseEntity<Void> putBookStatus(
			@PathVariable UUID bookId,
			@RequestParam @NotNull UUID userId,
			@RequestParam BookStatusType status
	) {
		bookStatusService.putStatus(bookId, userId, status);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@DeleteMapping("/{bookId}")
	public ResponseEntity<Void> deleteBookStatus(
			@PathVariable UUID bookId,
			@RequestParam @NotNull UUID userId
	) {
		bookStatusService.deleteStatus(bookId, userId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}

}
