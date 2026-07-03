package com.codeit.deokhugam.domain.dashboard.mapper;

import com.codeit.deokhugam.domain.dashboard.dto.response.CursorPageRankingResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PopularReviewResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PopularReview;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PopularReviewMapper {

    public PopularReviewResponse toResponse(PopularReview entity) {

        return new PopularReviewResponse(
            entity.getReviewId(),
            entity.getRanking(),
            entity.getBookTitle(),
            entity.getBookAuthor(),
            entity.getThumbnailUrl(),
            entity.getUserNickname(),
            entity.getReviewSummary(),
            entity.getReviewRating(),
            entity.getScore(),
            entity.getLikeCount(),
            entity.getCommentCount()
        );
    }

    public CursorPageRankingResponse<PopularReviewResponse> toCursorPageRankingResponse(
        List<PopularReview> entities,
        boolean hasNext,
        Integer nextMinRank
    ) {

        List<PopularReviewResponse> items = entities.stream()
            .map(this::toResponse)
            .toList();

        return new CursorPageRankingResponse<>(
            items,
            hasNext,
            nextMinRank
        );
    }
}
