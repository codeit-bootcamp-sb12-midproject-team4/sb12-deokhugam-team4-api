package com.codeit.deokhugam.domain.dashboard.dto;

import java.util.List;

public record FixedTopRankResponse<T>(
	List<T> content
) {
}
