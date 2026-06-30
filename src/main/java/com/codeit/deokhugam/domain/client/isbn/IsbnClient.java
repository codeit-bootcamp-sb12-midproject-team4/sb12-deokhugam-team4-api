package com.codeit.deokhugam.domain.client.isbn;

import com.codeit.deokhugam.domain.book.dto.BookIsbnResponse;

public interface IsbnClient {

	BookIsbnResponse getInfoFromIsbn(String isbn);

}
