package com.codeit.deokhugam.domain.dashboard.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.codeit.deokhugam.domain.dashboard.dto.response.TrendingKeywordResponse;
import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeyword;
import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeywordSnapshot;

@Component
public class TrendingKeywordMapper {

    public TrendingKeywordResponse toResponse(
            TrendingKeywordSnapshot snapshot,
            List<TrendingKeyword> keywords
    ) {

        return new TrendingKeywordResponse(
                snapshot.getSnapshotId(),
                snapshot.getCalculatedAt(),
                keywords.stream()
                        .map(this::toItem)
                        .toList()
        );
    }

    private TrendingKeywordResponse.TrendingKeywordItem toItem(
            TrendingKeyword keyword
    ) {

        return new TrendingKeywordResponse.TrendingKeywordItem(
                keyword.getRanking(),
                keyword.getKeyword(),
                keyword.getScore()
        );
    }
}
