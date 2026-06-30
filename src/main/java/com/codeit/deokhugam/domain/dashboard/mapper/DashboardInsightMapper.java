package com.codeit.deokhugam.domain.dashboard.mapper;

import org.springframework.stereotype.Component;

import com.codeit.deokhugam.domain.dashboard.dto.response.DashboardInsightResponse;

@Component
public class DashboardInsightMapper {

    public DashboardInsightResponse toResponse(String content) {
        return new DashboardInsightResponse(content);
    }
}
