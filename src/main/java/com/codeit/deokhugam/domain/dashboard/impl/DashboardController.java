package com.codeit.deokhugam.domain.dashboard.impl;

import java.awt.*;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.dashboard.dto.DashboardRequest;
import com.codeit.deokhugam.domain.dashboard.dto.PopularBookResponse;
import com.codeit.deokhugam.domain.dashboard.dto.PopularReviewResponse;
import com.codeit.deokhugam.domain.dashboard.dto.PowerUserResponse;
import com.codeit.deokhugam.domain.dashboard.dto.TrendingKeywordResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DashboardController {
	private final DashboardFacade dashboardFacade;
	private final DashboardService dashboardService;

	@GetMapping(value = "/books/popular")
	public ResponseEntity<CursorPageResponse<PopularBookResponse>> getPopularBooks(
		@ModelAttribute @Valid DashboardRequest req
	) {
		CursorPageResponse<PopularBookResponse> res = dashboardFacade.getPopularBooks(req);
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@GetMapping(value = "/reviews/popular")
	public ResponseEntity<CursorPageResponse<PopularReviewResponse>> getPopularReview(
		@ModelAttribute @Valid DashboardRequest req
	) {
		CursorPageResponse<PopularReviewResponse> res = dashboardFacade.getPopularReviews(req);
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@GetMapping(value = "/users/power")
	public ResponseEntity<CursorPageResponse<PowerUserResponse>> getPowerUser(
		@ModelAttribute @Valid DashboardRequest req
	) {
		req.setPeriod(PeriodType.DAILY); // 임시방편...
		CursorPageResponse<PowerUserResponse> res = dashboardService.getPowerUsers(req);
		return ResponseEntity.status(HttpStatus.OK).body(res);
	}

	@GetMapping(value = "/keywords/power")
	public ResponseEntity<List<TrendingKeywordResponse>> getTrendingKeyword(
		@ModelAttribute @Valid DashboardRequest req
	) {
		List<TrendingKeywordResponse> result = dashboardService.getLatestTrendingKeywords(req);
		return ResponseEntity.ok(result);
	}


}
