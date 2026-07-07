package com.codeit.deokhugam.domain.booksearch.event;

import java.util.UUID;

import lombok.Getter;

@Getter
public class BookUpdateEvent extends BookSyncEvent {
	private final String categoryPath;

	public BookUpdateEvent(UUID bookId, String categoryPath) {
		super(bookId);
		this.categoryPath = categoryPath;
	}
}
