package com.codeit.deokhugam.domain.dashboard.dto.response;

import java.util.List;

public record FixedTopRankResponse<T>(
	List<T> content
) {
}
