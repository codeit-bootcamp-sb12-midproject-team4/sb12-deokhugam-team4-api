package com.codeit.deokhugam.domain.dashboard.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TrendingKeywordResponse(
        Long snapshotId,
        LocalDateTime calculatedAt,
        List<TrendingKeywordItem> keywords
) {
    public record TrendingKeywordItem(
            Integer ranking,
            String keyword
    ) {}
}
