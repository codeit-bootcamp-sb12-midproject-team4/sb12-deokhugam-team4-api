package com.codeit.deokhugam.domain.dashboard.service;

import com.codeit.deokhugam.domain.dashboard.dto.response.CursorPageRankingResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PopularReviewResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PopularReview;
import com.codeit.deokhugam.domain.dashboard.mapper.PopularReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopularReviewService {

	private final PopularReviewCacheService cacheService;
	private final PopularReviewMapper mapper;

	/**
	 * 배치가 생성한 Top50 인기 리뷰 랭킹을 조회
	 * datasetId 단위로 전체 랭킹을 캐싱한 후 메모리에서 잘라 반환하는 Pseudo Paging 방식
	 */
	public CursorPageRankingResponse<PopularReviewResponse> getPopularReviews(
		Long datasetId,
		int minRank,
		int limit
	) {

		List<PopularReview> reviews = cacheService.getPopularReviews(datasetId);

		int fromIndex = Math.max(0, minRank - 1);

		if (fromIndex >= reviews.size()) {
			return mapper.toCursorPageRankingResponse(
				List.of(),
				false,
				null
			);
		}

		int toIndex = Math.min(fromIndex + limit, reviews.size());

		List<PopularReview> page = reviews.subList(fromIndex, toIndex);

		boolean hasNext = toIndex < reviews.size();

		Integer nextRank = hasNext
			? reviews.get(toIndex).getRanking()
			: null;

		return mapper.toCursorPageRankingResponse(
			page,
			hasNext,
			nextRank
		);
	}
}