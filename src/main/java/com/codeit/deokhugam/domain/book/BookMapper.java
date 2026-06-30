package com.codeit.deokhugam.domain.book;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.codeit.deokhugam.domain.book.dto.BookPostRequest;
import com.codeit.deokhugam.domain.book.dto.BookResponse;
import com.codeit.deokhugam.domain.bookstatus.BookStatus;
import com.codeit.deokhugam.domain.bookstatus.BookStatusType;

@Mapper(componentModel = "spring")
public interface BookMapper {

	@Mapping(target = "id",          	ignore = true)
	@Mapping(target = "createdAt",   	ignore = true)
	@Mapping(target = "updatedAt",   	ignore = true)
	@Mapping(target = "deletedAt",   	ignore = true)
	@Mapping(target = "thumbnailUrl", 	source = "url")
	@Mapping(target = "reviewCount", 	constant = "0L")
	@Mapping(target = "rating",		 	constant = "0.0")
	@Mapping(target = "bookCategory",	source = "category")
	Book toBook(BookPostRequest req, String url, BookCategory category);

	@Mapping(target = "id",				source = "book.id")
	@Mapping(target = "createdAt",		source = "book.createdAt")
	@Mapping(target = "updatedAt", 		source = "book.updatedAt")
	@Mapping(target = "categoryPath", 	source = "book.bookCategory.path")
	@Mapping(target = "status", 		source = "status", qualifiedByName = "mapStatus")
	BookResponse toResponse(Book book, BookStatus status);

	@Named("mapStatus")
	default BookStatusType mapStatus(BookStatus status) {
		return (status != null) ? status.getStatus() : null;
	}

	default LocalDateTime map(Instant instant) {	// -> createdAt, updatedAt Instant를 LocalDateTime으로 변환
		if (instant == null) {
			return null;
		}
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

}
