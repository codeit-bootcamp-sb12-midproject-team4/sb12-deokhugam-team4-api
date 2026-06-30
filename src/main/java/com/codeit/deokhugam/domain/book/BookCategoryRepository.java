package com.codeit.deokhugam.domain.book;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCategoryRepository extends JpaRepository<BookCategory, UUID> {

	Optional<BookCategory> findByPath(String path);

	Optional<BookCategory> findByNameAndParent(String name, BookCategory parent);

}
