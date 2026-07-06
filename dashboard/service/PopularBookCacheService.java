package com.codeit.deokhugam.domain.dashboard.service;

import com.codeit.deokhugam.domain.dashboard.entity.PopularBook;
import com.codeit.deokhugam.domain.dashboard.repository.PopularBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopularBookCacheService {

	private final PopularBookRepository repository;

	/**
	 * datasetId 기준 Top50 인기 도서 랭킹을 캐싱한다.
	 */
	@Cacheable(cacheNames = "dashboardPopularBooks", key = "#datasetId")
	public List<PopularBook> getPopularBooks(Long datasetId) {
		return repository.findByDatasetIdOrderByRankingAsc(datasetId);
	}
}