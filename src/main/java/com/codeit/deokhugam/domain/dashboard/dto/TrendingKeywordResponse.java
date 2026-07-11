package com.codeit.deokhugam.domain.dashboard.dto;

import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeyword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendingKeywordResponse {
	private Integer rank;
	private String keyword;
	private Double score;

	public static TrendingKeywordResponse from(TrendingKeyword entity) {
		return TrendingKeywordResponse.builder()
			.rank(entity.getRanking())
			.keyword(entity.getKeyword())
			.score(entity.getScore().doubleValue())
			.build();
	}
}
