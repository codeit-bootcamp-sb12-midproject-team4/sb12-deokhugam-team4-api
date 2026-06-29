package com.codeit.deokhugam.domain.client.isbn;

import org.springframework.stereotype.Component;

import com.codeit.deokhugam.domain.book.dto.BookIsbnResponse;

@Component
public class NaverApiClient implements IsbnClient {

	@Override
	public BookIsbnResponse getInfoFromIsbn(String isbn) {
		return null;
	}

}
