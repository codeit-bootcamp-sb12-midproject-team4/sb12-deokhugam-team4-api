package com.codeit.deokhugam.domain.dashboard.impl;

import org.springframework.stereotype.Service;

import com.codeit.deokhugam.domain.client.s3.FileStorageClient;
import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.dashboard.dto.DashboardRequest;
import com.codeit.deokhugam.domain.dashboard.dto.PopularBookResponse;
import com.codeit.deokhugam.domain.dashboard.dto.PopularReviewResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardFacade {
	private final DashboardService dashboardService;
	private final FileStorageClient fileStorageClient;

	public CursorPageResponse<PopularBookResponse> getPopularBooks(DashboardRequest req) {
		CursorPageResponse<PopularBookResponse> res = dashboardService.getPopularBooks(req);
		res.getContent().forEach(book -> {
			if (book.getThumbnailUrl() != null) {
				String imgUrl = fileStorageClient.getAttachFileUrl(book.getThumbnailUrl());
				book.setThumbnailUrl(imgUrl);
			}
		});
		return res;
	}

	public CursorPageResponse<PopularReviewResponse> getPopularReviews(DashboardRequest req) {
		CursorPageResponse<PopularReviewResponse> res = dashboardService.getPopularReviews(req);
		res.getContent().forEach(review -> {
			if (review.getBookThumbnailUrl() != null) {
				String imgUrl = fileStorageClient.getAttachFileUrl(review.getBookThumbnailUrl());
				review.setBookThumbnailUrl(imgUrl);
			}
		});
		return res;
	}

}
