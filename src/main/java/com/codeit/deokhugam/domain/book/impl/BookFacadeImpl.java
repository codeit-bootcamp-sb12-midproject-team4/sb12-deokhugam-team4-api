package com.codeit.deokhugam.domain.book.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.codeit.deokhugam.domain.book.BookFacade;
import com.codeit.deokhugam.domain.book.dto.BookPatchRequest;
import com.codeit.deokhugam.domain.book.dto.BookPostRequest;
import com.codeit.deokhugam.domain.book.dto.BookResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookFacadeImpl implements BookFacade {

	@Override
	public BookResponse post(BookPostRequest req, MultipartFile img) {
		// img가 있다면 -> AWS S3 저장 & URL 추출
		// isbn이 있다면 -> 카테고리 정보 조회 & BookCategory 생성
		// 도서 정보 저장
		return null;
	}

	@Override
	public BookResponse patch(UUID bookId, BookPatchRequest req, MultipartFile img) {
		// img가 있다면 -> 기존 AWS S3 이미지삭제 & 새로운 이미지 저장 & URL 추출
		// 도서 정보 수정
		return null;
	}

}
