package com.codeit.deokhugam.domain.book.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookPostRequest {

	@NotBlank(message = "도서 제목은 필수입니다.")
	@Size(max = 255, message = "도서 제목은 255자 이하입니다.")
	private String title;

	@NotBlank(message = "도서 저자명는 필수입니다.")
	@Size(max = 50, message = "도서 저자명은 50자 이하입니다.")
	private String author;

	@Nullable
	private String description;

	@NotBlank(message = "도서 출판사는 필수입니다.")
	@Size(max = 50, message = "도서 출판사는 50자 이하입니다.")
	private String publisher;

	@Nullable
	@PastOrPresent(message = "도서 출간일은 과거부터 현재 사이입니다.")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate publishedDate;

	@Nullable
	@Size(max = 20, message = "도서 ISBN정보는 20자 이하입니다.")
	@Pattern(regexp = "^(978|979)-?[0-9]{1,7}-?[0-9]{1,7}-?[0-9]{1,7}-?[0-9X0-9]$",
		message = "올바른 ISBN 형식이 아닙니다.")
	private String isbn;

}
