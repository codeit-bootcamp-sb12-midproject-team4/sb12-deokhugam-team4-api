package com.codeit.deokhugam.domain.dashboard.dto.response;

import java.time.LocalDate;
import java.util.List;
import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;

public record DashboardResponse(
        PeriodType period,
        LocalDate batchDate,
        List<PopularBookResponse> popularBook,
        List<PopularReviewResponse> popularReview,
        List<PowerUserResponse> powerUser,
        TrendingKeywordResponse trendingKeyword,
        DashboardInsightResponse insight
) {}
