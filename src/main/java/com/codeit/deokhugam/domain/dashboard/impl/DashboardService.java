package com.codeit.deokhugam.domain.dashboard.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.dashboard.PopularBookRepository;
import com.codeit.deokhugam.domain.dashboard.PopularReviewRepository;
import com.codeit.deokhugam.domain.dashboard.PowerUserRepository;
import com.codeit.deokhugam.domain.dashboard.TrendingKeywordRepository;
import com.codeit.deokhugam.domain.dashboard.TrendingKeywordSnapshotRepository;
import com.codeit.deokhugam.domain.dashboard.dto.DashboardRequest;
import com.codeit.deokhugam.domain.dashboard.dto.PopularBookResponse;
import com.codeit.deokhugam.domain.dashboard.dto.PopularReviewResponse;
import com.codeit.deokhugam.domain.dashboard.dto.PowerUserResponse;
import com.codeit.deokhugam.domain.dashboard.dto.TrendingKeywordResponse;
import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;
import com.codeit.deokhugam.domain.dashboard.entity.PopularBook;
import com.codeit.deokhugam.domain.dashboard.entity.PopularReview;
import com.codeit.deokhugam.domain.dashboard.entity.PowerUser;
import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeyword;
import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeywordSnapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
	private final PopularBookRepository popularBookRepository;
	private final PopularReviewRepository popularReviewRepository;
	private final PowerUserRepository powerUserRepository;
	private final TrendingKeywordRepository trendingKeywordRepository;
	private final TrendingKeywordSnapshotRepository snapshotRepository;

	public CursorPageResponse<PopularBookResponse> getPopularBooks(DashboardRequest req) {
		Optional<PopularBook> latestDatasetRecord = popularBookRepository.findFirstByPeriodOrderByDatasetIdDesc(req.getPeriod());
		if (latestDatasetRecord.isEmpty()) {
			log.info("--------인기도서조회 목록없음");
			return CursorPageResponse.empty(req.getLimit());
		}
		Long currentDatasetId = latestDatasetRecord.get().getDatasetId();

		int cursorRanking = (req.getCursor() == null || req.getCursor().isBlank()) ? 0 : Integer.parseInt(req.getCursor());

		PageRequest pageRequest = PageRequest.of(0, req.getLimit() + 1);
		List<PopularBook> fetchedBooks = popularBookRepository.findPopularBooksByCursor(currentDatasetId, cursorRanking, pageRequest);
		boolean hasNext = fetchedBooks.size() > req.getLimit();
		if (hasNext) {
			fetchedBooks.remove(req.getLimit()); // 초과 조회한 1개 제거
		}

		List<PopularBookResponse> content = fetchedBooks.stream()
			.map(this::toResponseDto)
			.collect(Collectors.toList());

		String nextCursor = null;
		java.time.Instant nextAfter = null;
		if (!content.isEmpty()) {
			PopularBookResponse lastElement = content.get(content.size() - 1);
			nextCursor = String.valueOf(lastElement.getRank());
			nextAfter = lastElement.getCreatedAt();
		}

		long totalElements = popularBookRepository.countByDatasetId(currentDatasetId);
		return CursorPageResponse.<PopularBookResponse>builder()
			.content(content)
			.nextCursor(nextCursor)
			.nextAfter(nextAfter)
			.size(req.getLimit())
			.totalElements(totalElements)
			.hasNext(hasNext)
			.build();
	}
	private PopularBookResponse toResponseDto(PopularBook entity) {
		return PopularBookResponse.builder()
			.id(entity.getId())
			.bookId(entity.getBookId())
			.title(entity.getBookTitle())
			.author(entity.getAuthor())
			.thumbnailUrl(entity.getThumbnailUrl())
			.period(entity.getPeriod())
			.rank(entity.getRanking())
			.score(entity.getScore() != null ? entity.getScore().doubleValue() : 0.0)
			.reviewCount(entity.getReviewCount() != null ? entity.getReviewCount().longValue() : 0L)
			.rating(entity.getAverageRating() != null ? entity.getAverageRating().doubleValue() : 0.0)
			.createdAt(entity.getCreatedAt()) // BaseEntity에 정의되어 있다고 가정
			.build();
	}

	public CursorPageResponse<PopularReviewResponse> getPopularReviews(DashboardRequest req) {

		Optional<PopularReview> latestDatasetOpt = popularReviewRepository.findFirstByPeriodOrderByDatasetIdDesc(req.getPeriod());
		if (latestDatasetOpt.isEmpty()) {
			log.info("--------인기리뷰조회 목록없음");
			return CursorPageResponse.empty(req.getLimit());
		}

		Long currentDatasetId = latestDatasetOpt.get().getDatasetId();

		int cursorRanking = (req.getCursor() == null || req.getCursor().isBlank()) ? 0 : Integer.parseInt(req.getCursor());

		PageRequest pageRequest = PageRequest.of(0, req.getLimit() + 1);
		List<PopularReview> fetchedReviews = popularReviewRepository.findPopularReviewsByCursor(currentDatasetId, cursorRanking, pageRequest);

		boolean hasNext = fetchedReviews.size() > req.getLimit();
		if (hasNext) {
			fetchedReviews.remove(req.getLimit());
		}

		List<PopularReviewResponse> content = fetchedReviews.stream()
			.map(this::toResponseDto)
			.collect(Collectors.toList());

		String nextCursor = null;
		java.time.Instant nextAfter = null;
		if (!content.isEmpty()) {
			PopularReviewResponse lastElement = content.get(content.size() - 1);
			nextCursor = String.valueOf(lastElement.getRank());
			nextAfter = lastElement.getCreatedAt();
		}
		long totalElements = popularReviewRepository.countByDatasetId(currentDatasetId);

		return CursorPageResponse.<PopularReviewResponse>builder()
			.content(content)
			.nextCursor(nextCursor)
			.nextAfter(nextAfter)
			.size(req.getLimit())
			.totalElements(totalElements)
			.hasNext(hasNext)
			.build();
	}
	private PopularReviewResponse toResponseDto(PopularReview entity) {
		return PopularReviewResponse.builder()
			.id(entity.getId())
			.reviewId(entity.getReviewId())
			.bookId(null)
			.userId(null)
			.bookTitle(entity.getBookTitle())
			.bookThumbnailUrl(entity.getThumbnailUrl())
			.userNickname(entity.getUserNickname())
			.reviewContent(entity.getReviewSummary())
			.reviewRating(entity.getReviewRating() != null ? entity.getReviewRating().doubleValue() : 0.0)
			.period(entity.getPeriod())
			.createdAt(entity.getCreatedAt())
			.rank(entity.getRanking())
			.score(entity.getScore() != null ? entity.getScore().doubleValue() : 0.0)
			.likeCount(entity.getLikeCount() != null ? entity.getLikeCount().longValue() : 0L)
			.commentCount(entity.getCommentCount() != null ? entity.getCommentCount().longValue() : 0L)
			.build();
	}

	public CursorPageResponse<PowerUserResponse> getPowerUsers(DashboardRequest req) {
		Optional<PowerUser> latestDatasetOpt = powerUserRepository.findFirstByPeriodOrderByDatasetIdDesc(req.getPeriod());
		if (latestDatasetOpt.isEmpty()) {
			log.info("--------파워유저조회 목록없음");
			return CursorPageResponse.empty(req.getLimit());
		}

		Long currentDatasetId = latestDatasetOpt.get().getDatasetId();

		int cursorRanking = (req.getCursor() == null || req.getCursor().isBlank()) ? 0 : Integer.parseInt(req.getCursor());

		PageRequest pageRequest = PageRequest.of(0, req.getLimit() + 1);
		List<PowerUser> fetchedUsers = powerUserRepository.findPowerUsersByCursor(currentDatasetId, cursorRanking, pageRequest);

		boolean hasNext = fetchedUsers.size() > req.getLimit();
		if (hasNext) {
			fetchedUsers.remove(req.getLimit());
		}

		List<PowerUserResponse> content = fetchedUsers.stream()
			.map(this::toResponseDto)
			.collect(Collectors.toList());

		String nextCursor = null;
		java.time.Instant nextAfter = null;
		if (!content.isEmpty()) {
			PowerUserResponse lastElement = content.get(content.size() - 1);
			nextCursor = String.valueOf(lastElement.getRank());
			nextAfter = lastElement.getCreatedAt();
		}
		long totalElements = powerUserRepository.countByDatasetId(currentDatasetId);

		return CursorPageResponse.<PowerUserResponse>builder()
			.content(content)
			.nextCursor(nextCursor)
			.nextAfter(nextAfter)
			.size(req.getLimit())
			.totalElements(totalElements)
			.hasNext(hasNext)
			.build();
	}
	private PowerUserResponse toResponseDto(PowerUser entity) {
		return PowerUserResponse.builder()
			.userId(entity.getUserId())
			.nickname(entity.getNickname())
			.period(entity.getPeriod())
			.createdAt(entity.getCreatedAt())
			.rank(entity.getRanking())
			.score(entity.getScore() != null ? entity.getScore().doubleValue() : 0.0)
			.reviewScoreSum(0.0)
			.likeCount(entity.getLikeCount() != null ? entity.getLikeCount().longValue() : 0L)
			.commentCount(entity.getCommentCount() != null ? entity.getCommentCount().longValue() : 0L)
			.build();
	}


	public List<TrendingKeywordResponse> getLatestTrendingKeywords(DashboardRequest req) {

		Optional<TrendingKeywordSnapshot> latestSnapshot = snapshotRepository.findFirstByOrderByDatasetIdDesc();

		if (latestSnapshot.isEmpty()) {
			return List.of();
		}

		return trendingKeywordRepository.findTop10BySnapshot_DatasetIdOrderByRankingAsc(latestSnapshot.get().getDatasetId())
			.stream()
			.map(TrendingKeywordResponse::from)
			.collect(Collectors.toList());

	}

}
