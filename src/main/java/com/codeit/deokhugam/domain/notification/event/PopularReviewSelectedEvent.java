package com.codeit.deokhugam.domain.notification.event;

import com.codeit.deokhugam.domain.dashboard.PeriodType;
import java.util.UUID;

public record PopularReviewSelectedEvent(
    UUID reviewId,
    PeriodType period
) {

}
