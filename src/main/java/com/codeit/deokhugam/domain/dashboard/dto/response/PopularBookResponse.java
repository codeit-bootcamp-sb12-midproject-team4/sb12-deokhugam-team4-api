package com.codeit.deokhugam.domain.dashboard.dto.response;

import java.util.UUID;

public record PopularBookResponse(
        UUID bookId,
        String title,
        String author,
        Integer ranking,
        Long viewCount
) {}
