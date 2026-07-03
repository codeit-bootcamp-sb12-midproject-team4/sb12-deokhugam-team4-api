package com.codeit.deokhugam.domain.dashboard.service;

import com.codeit.deokhugam.domain.dashboard.dto.response.CursorPageRankingResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PopularBookResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PopularBook;
import com.codeit.deokhugam.domain.dashboard.mapper.PopularBookMapper;
import com.codeit.deokhugam.domain.dashboard.repository.PopularBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopularBookService {

	private final PopularBookRepository repository;
	private final PopularBookMapper mapper;

	/**
	 * 배치가 생성한 Top50 인기 도서 랭킹을 조회
	 * datasetId 단위로 전체 랭킹을 캐싱한 후 메모리에서 잘라 반환하는 Pseudo Paging 방식
	 */
	public CursorPageRankingResponse<PopularBookResponse> getPopularBooks(
		Long datasetId,
		int minRank,
		int limit
	) {

		List<PopularBook> books = getCachedPopularBooks(datasetId);

		int fromIndex = Math.max(0, minRank - 1);

		if (fromIndex >= books.size()) {
			return mapper.toCursorPageRankingResponse(
				List.of(),
				false,
				null
			);
		}

		int toIndex = Math.min(fromIndex + limit, books.size());

		List<PopularBook> target = books.subList(fromIndex, toIndex);

		boolean hasNext = toIndex < books.size();

		Integer nextMinRank = hasNext
			? books.get(toIndex).getRanking()
			: null;

		return mapper.toCursorPageRankingResponse(
			target,
			hasNext,
			nextMinRank
		);
	}

	@Cacheable(cacheNames = "dashboardPopularBooks", key = "#datasetId")
	protected List<PopularBook> getCachedPopularBooks(Long datasetId) {
		return repository.findByDatasetIdOrderByRankingAsc(datasetId);
	}
}