package com.codeit.deokhugam.domain.booksearch.event;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.scheduling.annotation.Async;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.book.BookRepository;
import com.codeit.deokhugam.domain.booksearch.BookDocument;

import lombok.RequiredArgsConstructor;

@Component
@Profile({"dev", "dev-batch", "prod", "test-es"})
@RequiredArgsConstructor
public class BookSyncEventListener {
	private final BookRepository bookRepository;
	private final ElasticsearchOperations esOperations;

	@Async("esSyncExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleBookCreated(BookCreateEvent event) {
		Book book = bookRepository.findById(event.getBookId())
			.orElseThrow(() -> new IllegalArgumentException("도서생성 동기화 중 도서를 찾을 수 없습니다. ID: " + event.getBookId()));
		BookDocument document = BookDocument.builder()
			.id(book.getId().toString())
			.title(book.getTitle())
			.author(book.getAuthor())
			.description(book.getDescription())
			.titleSuggest(book.getTitle())
			.publisher(book.getPublisher())
			.categoryPath(event.getCategoryPath())
			.reviewCount(book.getReviewCount())
			.rating(book.getRating())
			.publishedDate(book.getPublishedDate().toString())
			.isbn(book.getIsbn())
			.thumbnailKey(book.getThumbnailKey())
			.createdAt(book.getCreatedAt())
			.updatedAt(book.getUpdatedAt())
			.build();
		esOperations.save(document);
	}

	@Async("esSyncExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleBookUpdated(BookUpdateEvent event) {
		Book book = bookRepository.findById(event.getBookId())
			.orElseThrow(() -> new IllegalArgumentException("도서갱신 동기화 중 도서를 찾을 수 없습니다. ID: " + event.getBookId()));
		Document doc = Document.create();
		doc.put("title", book.getTitle());
		doc.put("author", book.getAuthor());
		doc.put("description", book.getDescription());
		doc.put("titleSuggest", book.getTitle());
		doc.put("publisher", book.getPublisher());
		doc.put("categoryPath", event.getCategoryPath());
		doc.put("isbn", book.getIsbn());
		doc.put("thumbnailKey", book.getThumbnailKey());
		if (book.getPublishedDate() != null) {
			doc.put("publishedDate", book.getPublishedDate().toString());
		}
		if (book.getUpdatedAt() != null) {
			doc.put("updatedAt", book.getUpdatedAt().toString());
		}
		UpdateQuery updateQuery = UpdateQuery.builder(book.getId().toString())
			.withDocument(doc)
			.build();
		esOperations.update(updateQuery, IndexCoordinates.of("books"));
	}

	@Async("esSyncExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleBookDeleted(BookDeleteEvent event) {
		esOperations.delete(event.getBookId().toString(), BookDocument.class);
	}
}
