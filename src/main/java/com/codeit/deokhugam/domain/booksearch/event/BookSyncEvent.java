package com.codeit.deokhugam.domain.booksearch.event;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BookSyncEvent {
	private final UUID bookId;
}
