package com.codeit.deokhugam.domain.dashboard.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record TrendingKeywordResponse(
        Long snapshotId,
        LocalDateTime calculatedAt,
        List<KeywordElement> keyword
) {
    public record KeywordElement(
            Integer ranking,
            String keyword,
            Long searchCount
    ) {}
}
