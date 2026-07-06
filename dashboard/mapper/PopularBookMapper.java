package com.codeit.deokhugam.domain.dashboard.mapper;

import com.codeit.deokhugam.domain.dashboard.dto.response.CursorPageRankingResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PopularBookResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PopularBook;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public CursorPageRankingResponse<PopularBookResponse> toCursorPageRankingResponse(
        List<PopularBook> entities,
        boolean hasNext,
        Integer nextRank
    ) {

        List<PopularBookResponse> content = entities.stream()
            .map(this::toResponse)
            .toList();

        return new CursorPageRankingResponse<>(
            content,
            nextRank,
            hasNext
        );
    }
}
