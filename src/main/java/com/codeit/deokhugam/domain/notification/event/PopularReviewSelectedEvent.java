package com.codeit.deokhugam.domain.notification.event;

import java.util.UUID;

import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;

public record PopularReviewSelectedEvent(
	UUID reviewId,
	PeriodType period
) {

}
