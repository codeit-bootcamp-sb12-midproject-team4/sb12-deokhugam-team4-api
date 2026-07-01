package com.codeit.deokhugam.domain.bookstatus;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookStatusType {
	WANT("WANT"),
	READING("READING"),
	DONE("DONE");

	private final String value;

	@JsonCreator
	public static BookStatusType from(String value) {
		return Arrays.stream(values())
			.filter(status -> status.getValue().equals(value))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("알 수 없는 도서 상태값: " + value));
	}

	@JsonValue
	public String getValue() {
		return value;
	}


}
