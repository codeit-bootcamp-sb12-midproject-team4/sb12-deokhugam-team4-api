package com.codeit.deokhugam.domain.review.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {

	@NotNull
	private UUID bookId;

	@NotNull
	private UUID userId;

	@NotBlank
	@Size(max = 1000)
	private String content;

	@NotNull
	@Min(1)
	@Max(5)
	private Integer rating;
}
