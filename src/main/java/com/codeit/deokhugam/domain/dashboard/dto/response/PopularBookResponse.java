package com.codeit.deokhugam.domain.dashboard.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PopularBookResponse(
        UUID bookId,
        Integer ranking,
        String title,
        String author,
        String thumbnailUrl,
        BigDecimal score,
        Integer reviewCount,
        Integer likeCount,
        Integer commentCount,
        BigDecimal averageRating
) {}
