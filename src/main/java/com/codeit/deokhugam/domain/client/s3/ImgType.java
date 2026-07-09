package com.codeit.deokhugam.domain.client.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ImgType {

	PREFIX_BOOK("thumbnail"),
	PREFIX_REVIEW("reviewpicture");

	private final String field;
}
