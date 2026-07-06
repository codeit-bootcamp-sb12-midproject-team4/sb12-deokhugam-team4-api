package com.codeit.deokhugam.domain.dashboard.dto.response;

import java.util.List;

public record CursorPageRankingResponse<T>(
	List<T> content,
	Integer nextRank,
	boolean hasNext
) {
}