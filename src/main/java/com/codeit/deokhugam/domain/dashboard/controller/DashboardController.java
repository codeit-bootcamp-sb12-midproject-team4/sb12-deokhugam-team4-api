package com.codeit.deokhugam.domain.dashboard.controller;

import com.codeit.deokhugam.domain.dashboard.dto.response.CursorPageRankingResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.FixedTopRankResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.KeywordListResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PopularBookResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PopularReviewResponse;
import com.codeit.deokhugam.domain.dashboard.dto.response.PowerUserResponse;
import com.codeit.deokhugam.domain.dashboard.entity.BatchMetadataType;
import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;
import com.codeit.deokhugam.domain.dashboard.service.DashboardQueryService;
import com.codeit.deokhugam.domain.dashboard.service.PopularBookService;
import com.codeit.deokhugam.domain.dashboard.service.PopularReviewService;
import com.codeit.deokhugam.domain.dashboard.service.PowerUserService;
import com.codeit.deokhugam.domain.dashboard.service.TrendingKeywordService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Validated
public class DashboardController {

	private final DashboardQueryService dashboardQueryService;
	private final PopularBookService popularBookService;
	private final PopularReviewService popularReviewService;
	private final PowerUserService powerUserService;
	private final TrendingKeywordService trendingKeywordService;

	@GetMapping("/book")
	public ResponseEntity<CursorPageRankingResponse<PopularBookResponse>> getPopularBooks(
		@RequestParam @NotNull PeriodType period,
		@RequestParam(defaultValue = "1") @Min(1) int minRank,
		@RequestParam(defaultValue = "5") @Min(1) int limit
	) {

		Long datasetId = dashboardQueryService.getDatasetId(
			BatchMetadataType.POPULAR_BOOK,
			period
		);

		return ResponseEntity.ok(
			popularBookService.getPopularBooks(
				datasetId,
				minRank,
				limit
			)
		);
	}

	@GetMapping("/review")
	public ResponseEntity<CursorPageRankingResponse<PopularReviewResponse>> getPopularReviews(
		@RequestParam @NotNull PeriodType period,
		@RequestParam(defaultValue = "1") @Min(1) int minRank,
		@RequestParam(defaultValue = "5") @Min(1) int limit
	) {

		Long datasetId = dashboardQueryService.getDatasetId(
			BatchMetadataType.POPULAR_REVIEW,
			period
		);

		return ResponseEntity.ok(
			popularReviewService.getPopularReviews(
				datasetId,
				minRank,
				limit
			)
		);
	}

	@GetMapping("/power-user")
	public ResponseEntity<FixedTopRankResponse<PowerUserResponse>> getPowerUsers(
		@RequestParam @NotNull PeriodType period
	) {

		Long datasetId = dashboardQueryService.getDatasetId(
			BatchMetadataType.POWER_USER,
			period
		);

		return ResponseEntity.ok(
			powerUserService.getPowerUsers(datasetId)
		);
	}

	@GetMapping("/trending-keyword")
	public ResponseEntity<KeywordListResponse> getTrendingKeywords() {

		Long datasetId = dashboardQueryService.getDatasetId(
			BatchMetadataType.TRENDING_KEYWORD
		);

		return ResponseEntity.ok(
			trendingKeywordService.getTrendingKeywords(datasetId)
		);
	}
}
