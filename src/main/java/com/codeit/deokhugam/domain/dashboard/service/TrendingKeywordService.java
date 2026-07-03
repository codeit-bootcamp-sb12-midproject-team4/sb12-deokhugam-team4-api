package com.codeit.deokhugam.domain.dashboard.service;

import com.codeit.deokhugam.domain.dashboard.dto.response.KeywordListResponse;
import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeyword;
import com.codeit.deokhugam.domain.dashboard.mapper.TrendingKeywordMapper;
import com.codeit.deokhugam.domain.dashboard.repository.TrendingKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrendingKeywordService {

	private final TrendingKeywordRepository repository;
	private final TrendingKeywordMapper mapper;

	/**
	 * 10분마다 생성된 Trending Keyword Top10을 조회한다.
	 * datasetId 단위로 캐싱하며 항상 Top10 전체를 반환한다.
	 */
	@Cacheable(
		cacheNames = "dashboardTrendingKeywords",
		key = "#datasetId"
	)
	public KeywordListResponse getTrendingKeywords(Long datasetId) {

		List<TrendingKeyword> keywords =
			repository.findAllByDatasetIdOrderByRankingAsc(datasetId);

		return mapper.toKeywordListResponse(keywords);
	}
}
