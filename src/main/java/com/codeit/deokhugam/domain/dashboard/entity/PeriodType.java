package com.codeit.deokhugam.domain.dashboard.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PeriodType {

    DAILY("DAILY", "일간"),
    WEEKLY("WEEKLY", "주간"),
    MONTHLY("MONTHLY", "월간"),
    ALL_TIME("ALL_TIME", "전체");

    private final String code;
    private final String description;

}