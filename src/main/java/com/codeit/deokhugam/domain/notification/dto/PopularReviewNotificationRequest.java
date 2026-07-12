package com.codeit.deokhugam.domain.notification.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PopularReviewNotificationRequest(
	@NotEmpty List<@NotNull UUID> reviewIds,
	@NotBlank String period
) {
}
