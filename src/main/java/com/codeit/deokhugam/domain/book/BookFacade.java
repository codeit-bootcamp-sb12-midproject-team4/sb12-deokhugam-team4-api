package com.codeit.deokhugam.domain.book;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.codeit.deokhugam.domain.book.dto.BookPatchRequest;
import com.codeit.deokhugam.domain.book.dto.BookPostRequest;
import com.codeit.deokhugam.domain.book.dto.BookResponse;

public interface BookFacade {

	BookResponse post(BookPostRequest req, MultipartFile img);

	BookResponse patch(UUID bookId, BookPatchRequest req, MultipartFile img);

}
