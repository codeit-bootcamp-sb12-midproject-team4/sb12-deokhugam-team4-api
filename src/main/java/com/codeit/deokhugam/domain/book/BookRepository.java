package com.codeit.deokhugam.domain.book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.bookstatus.BookStatusType;

public interface BookRepository extends JpaRepository<Book, UUID>, BookQueryRepository {

	@Query("""
			SELECT b
			FROM Book b JOIN FETCH b.bookCategory
			WHERE b.id = :id
	""")
	Optional<Book> findByIdWithCategory(@Param("id") UUID id);

	@Query("""
			SELECT new com.codeit.deokhugam.domain.book.dto.BookResponse(
				b.id,
				b.title,
				b.author,
				b.description,
				b.publisher,
				b.publishedDate,
				b.isbn,
				b.thumbnailKey,
				b.reviewCount,
				b.rating,
				bc.path,
				bs.status,
				b.createdAt,
				b.updatedAt
			)
			FROM Book b
				LEFT JOIN b.bookCategory bc
				LEFT JOIN BookStatus bs ON bs.book.id = b.id AND bs.user.id = :userId
			WHERE b.id = :bookId
	""")
	Optional<BookResponse> findByIdWithStatus(@Param("bookId") UUID bookId, @Param("userId") UUID userId);

	boolean existsByIsbn(String isbn);

}
