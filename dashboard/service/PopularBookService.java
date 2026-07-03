package com.codeit.deokhugam.domain.dashboard.service;

import com.codeit.deokhugam.domain.dashboard.dto.response.CursorPageRankingResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PopularBookResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PopularBook;
import com.codeit.deokhugam.domain.dashboard.mapper.PopularBookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopularBookService {

	private final PopularBookCacheService cacheService;
	private final PopularBookMapper mapper;

	public CursorPageRankingResponse<PopularBookResponse> getPopularBooks(
		Long datasetId,
		int minRank,
		int limit
	) {

		List<PopularBook> books = cacheService.getPopularBooks(datasetId);
		int fromIndex = Math.max(0, minRank - 1);
		if (fromIndex >= books.size()) {
			return mapper.toCursorPageRankingResponse(
				List.of(),
				false,
				null
			);
		}

		int toIndex = Math.min(fromIndex + limit, books.size());
		List<PopularBook> page = books.subList(fromIndex, toIndex);
		boolean hasNext = toIndex < books.size();
		Integer nextRank = hasNext
			? books.get(toIndex).getRanking()
			: null;

		return mapper.toCursorPageRankingResponse(
			page,
			hasNext,
			nextRank
		);
	}
}