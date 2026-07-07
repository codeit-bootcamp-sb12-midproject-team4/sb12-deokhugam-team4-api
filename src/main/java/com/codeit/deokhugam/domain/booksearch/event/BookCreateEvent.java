package com.codeit.deokhugam.domain.booksearch.event;

import java.util.UUID;

import lombok.Getter;

@Getter
public class BookCreateEvent extends BookSyncEvent {
	private final String categoryPath;

	public BookCreateEvent(UUID bookId, String categoryPath) {
		super(bookId);
		this.categoryPath = categoryPath;
	}
}
