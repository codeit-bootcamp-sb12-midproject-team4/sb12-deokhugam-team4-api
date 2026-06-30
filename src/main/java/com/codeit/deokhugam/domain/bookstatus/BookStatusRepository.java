package com.codeit.deokhugam.domain.bookstatus;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookStatusRepository extends JpaRepository<BookStatus, UUID> {

	Optional<BookStatus> findByBookIdAndUserId(UUID bookId, UUID userId);

	void deleteByBookIdAndUserId(UUID bookId, UUID userId);

}
