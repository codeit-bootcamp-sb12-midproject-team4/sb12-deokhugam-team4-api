package com.codeit.deokhugam.domain.dashboard.mapper;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.codeit.deokhugam.domain.dashboard.dto.response.DashboardInsightResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.DashboardResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PopularBookResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PopularReviewResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PowerUserResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.TrendingKeywordResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;

@Component
public class DashboardMapper {

    public DashboardResponse toResponse(
            PeriodType period,
            LocalDate batchDate,
            List<PopularBookResponse> books,
            List<PopularReviewResponse> reviews,
            List<PowerUserResponse> users,
            TrendingKeywordResponse keyword,
            DashboardInsightResponse insight
    ) {

        return new DashboardResponse(
                period,
                batchDate,
                books,
                reviews,
                users,
                keyword,
                insight
        );
    }
}
