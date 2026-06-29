package com.codeit.deokhugam.domain.bookstatus;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-status")
public class BookStatusController {

	@PutMapping("/{bookId}")
	public ResponseEntity<Void> putBookStatus(
			@PathVariable UUID bookId,
			@RequestParam @Pattern(regexp = "WANT|READING|DONE") String status
	) {
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	@DeleteMapping("/{bookId}")
	public ResponseEntity<Void> deleteBookStatus(@PathVariable UUID bookId) {
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
	}

}
