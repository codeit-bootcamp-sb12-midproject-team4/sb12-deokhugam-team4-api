package com.codeit.deokhugam.domain.dashboard.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TrendingKeywordResponse(
        Long snapshotId,
        LocalDateTime calculatedAt,
        List<TrendingKeywordItem> keyword
) {
    public record TrendingKeywordItem(
            Integer ranking,
            String keyword,
            BigDecimal score
    ) {}
}
