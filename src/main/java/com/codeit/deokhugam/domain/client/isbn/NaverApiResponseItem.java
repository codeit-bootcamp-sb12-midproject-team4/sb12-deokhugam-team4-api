package com.codeit.deokhugam.domain.client.isbn;

import java.time.LocalDate;

import org.springframework.cglib.core.Local;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverApiResponseItem {
	private String title;
	private String image;
	private String author;
	private String publisher;
	private String pubdate;
	private String isbn;
	private String description;
}
