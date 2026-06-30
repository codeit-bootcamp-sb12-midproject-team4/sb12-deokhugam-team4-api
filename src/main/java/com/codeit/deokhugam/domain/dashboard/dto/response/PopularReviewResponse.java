package com.codeit.deokhugam.domain.dashboard.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PopularReviewResponse(
        UUID reviewId,
        Integer ranking,
        String bookTitle,
        String bookAuthor,
        String thumbnailUrl,
        String userNickname,
        String reviewSummary,
        Integer reviewRating,
        BigDecimal score,
        Integer likeCount,
        Integer commentCount

) {}
