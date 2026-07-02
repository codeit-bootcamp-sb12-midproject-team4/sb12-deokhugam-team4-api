package com.codeit.deokhugam.domain.dashboard.mapper;

import org.springframework.stereotype.Component;

import com.codeit.deokhugam.domain.dashboard.dto.response.PopularReviewResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PopularReview;

@Component
public class PopularReviewMapper {

    public PopularReviewResponse toResponse(
        PopularReview entity,
        String reviewSummary
    ) {
        return new PopularReviewResponse(
            entity.getReviewId(),
            entity.getRanking(),
            entity.getBookTitle(),
            entity.getBookAuthor(),
            entity.getThumbnailUrl(),
            entity.getUserNickname(),
            reviewSummary,
            entity.getReviewRating(),
            entity.getScore(),
            entity.getLikeCount(),
            entity.getCommentCount()
        );
    }
}