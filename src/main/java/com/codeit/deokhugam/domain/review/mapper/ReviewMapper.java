package com.codeit.deokhugam.domain.review.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.codeit.deokhugam.domain.book.Book;
import com.codeit.deokhugam.domain.review.dto.ReviewCreateRequest;
import com.codeit.deokhugam.domain.review.dto.ReviewResponse;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.user.User;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

	@Mapping(target = "book", source = "book")
	@Mapping(target = "user", source = "user")
	@Mapping(target = "content", source = "request.content")
	@Mapping(target = "rating", source = "request.rating")
	@Mapping(target = "likeCount", ignore = true)
	@Mapping(target = "commentCount", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "attachmentUrl", ignore = true)
	Review toEntity(ReviewCreateRequest request, Book book, User user);

	@Mapping(target = "bookId", source = "review.book.id")
	@Mapping(target = "bookTitle", source = "review.book.title")
	@Mapping(target = "bookThumbnailUrl", source = "review.book.thumbnailUrl")
	@Mapping(target = "userId", source = "review.user.id")
	@Mapping(target = "userNickname", source = "review.user.nickname")
	@Mapping(target = "likedByMe", expression = "java(likedByMe)")
	ReviewResponse toResponse(Review review, @Context boolean likedByMe);
}
