package com.codeit.deokhugam.domain.review.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.codeit.deokhugam.domain.review.Review;
import com.codeit.deokhugam.domain.review.dto.ReviewResponse;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

	@Mapping(target = "bookId", source = "review.book.id")
	@Mapping(target = "bookTitle", source = "review.book.title")
	@Mapping(target = "bookThumbnailUrl", source = "review.book.thumbnailUrl")
	@Mapping(target = "userId", source = "review.user.id")
	@Mapping(target = "userNickname", source = "review.user.nickname")
	@Mapping(target = "likedByMe", expression = "java(likedByMe)")
	ReviewResponse toResponse(Review review, @Context boolean likedByMe);
}
