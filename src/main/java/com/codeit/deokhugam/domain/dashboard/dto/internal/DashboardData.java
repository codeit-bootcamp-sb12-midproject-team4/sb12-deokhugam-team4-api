package com.codeit.deokhugam.domain.dashboard.dto.internal;

import java.util.List;

import com.codeit.deokhugam.domain.dashboard.entity.PopularBook;
import com.codeit.deokhugam.domain.dashboard.entity.PopularReview;
import com.codeit.deokhugam.domain.dashboard.entity.PowerUser;
import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeyword;
import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeywordSnapshot;

public record DashboardData(
	List<PopularBook> popularBooks,
	List<PopularReview> popularReviews,
	List<PowerUser> powerUsers,
	TrendingKeywordSnapshot trendingKeywordSnapshot,
	List<TrendingKeyword> trendingKeywords
) {
}
