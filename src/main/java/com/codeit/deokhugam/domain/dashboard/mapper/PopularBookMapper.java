package com.codeit.deokhugam.domain.dashboard.mapper;

import org.springframework.stereotype.Component;

import com.codeit.deokhugam.domain.dashboard.dto.response.PopularBookResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PopularBook;

@Component
public class PopularBookMapper {

    public PopularBookResponse toResponse(PopularBook entity) {
        return new PopularBookResponse(
                entity.getBookId(),
                entity.getRanking(),
                entity.getBookTitle(),
                entity.getAuthor(),
                entity.getThumbnailUrl(),
                entity.getScore(),
                entity.getReviewCount(),
                entity.getLikeCount(),
                entity.getCommentCount(),
                entity.getAverageRating()
        );
    }
}
