package com.codeit.deokhugam.domain.dashboard.dto.response;

import java.time.LocalDate;
import java.util.List;
import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;

public record DashboardResponse(
        PeriodType period,
        LocalDate batchDate,
        List<PopularBookResponse> popularBooks,
        List<PopularReviewResponse> popularReviews,
        List<PowerUserResponse> powerUsers,
        TrendingKeywordResponse trendingKeyword,
        DashboardInsightResponse insight
) {}
