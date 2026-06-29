package com.codeit.deokhugam.domain.book;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.codeit.deokhugam.domain.book.dto.BookPostRequest;

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

}
