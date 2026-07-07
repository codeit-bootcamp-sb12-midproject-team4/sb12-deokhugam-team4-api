package com.codeit.deokhugam.domain.booksearch.event;

import java.util.UUID;

import lombok.Getter;

@Getter
public class BookDeleteEvent extends BookSyncEvent{

	public BookDeleteEvent(UUID bookId) {
		super(bookId);
	}
}
