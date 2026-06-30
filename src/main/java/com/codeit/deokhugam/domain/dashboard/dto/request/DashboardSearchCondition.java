package com.codeit.deokhugam.domain.dashboard.dto.request;

import java.time.LocalDate;

import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record DashboardSearchCondition(
	@NotNull(message = "조회 기간(PeriodType)은 필수입니다.")
	PeriodType period,

	@PastOrPresent(message = "배치 조회일은 미래 날짜일 수 없습니다.")
	LocalDate batchDate
) {
}
