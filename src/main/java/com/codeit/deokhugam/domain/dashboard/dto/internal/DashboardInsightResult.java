package com.codeit.deokhugam.domain.dashboard.dto.internal;

import java.util.List;

// normalizedKeywords : LLM이 정제한 표준 키워드 Top 10
public record DashboardInsightResult(
	String insight,
	List<NormalizedKeyword> normalizedKeywords
) {
	public record NormalizedKeyword(
		Integer ranking,
		String keyword
	) {
	}
}
