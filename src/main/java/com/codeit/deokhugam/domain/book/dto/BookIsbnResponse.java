package com.codeit.deokhugam.domain.book.dto;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookIsbnResponse {
	private String title;
	private String author;
	private String description;
	private String publisher;
	private LocalDate publishedDate;
	private String isbn;
	private String thumbnailImage;
}
