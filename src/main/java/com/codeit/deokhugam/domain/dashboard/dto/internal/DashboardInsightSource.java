package com.codeit.deokhugam.domain.dashboard.dto.internal;

import java.util.List;

import com.codeit.deokhugam.domain.dashboard.entity.PopularBook;
import com.codeit.deokhugam.domain.dashboard.entity.PopularReview;
import com.codeit.deokhugam.domain.dashboard.entity.PowerUser;

public record DashboardInsightSource(
	List<PopularBook> popularBooks,
	List<PopularReview> popularReviews,
	List<PowerUser> powerUsers,
	List<String> rawTrendingKeywords
) {}
