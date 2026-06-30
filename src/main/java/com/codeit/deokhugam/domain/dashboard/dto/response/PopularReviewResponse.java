package com.codeit.deokhugam.domain.dashboard.dto.response;

import java.util.UUID;

public record PopularReviewResponse(
        UUID reviewId,
        UUID bookId,
        String bookTitle,
        String memberNickname,
        String contentSummary,
        Integer ranking,
        Long likeCount
) {}
