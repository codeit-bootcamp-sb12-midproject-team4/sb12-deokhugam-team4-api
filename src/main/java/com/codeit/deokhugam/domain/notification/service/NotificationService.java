package com.codeit.deokhugam.domain.notification.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort.Direction;

import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.notification.dto.NotificationResponse;
import com.codeit.deokhugam.domain.notification.event.CommentCreatedEvent;
import com.codeit.deokhugam.domain.notification.event.PopularReviewSelectedEvent;
import com.codeit.deokhugam.domain.notification.event.ReviewLikedEvent;

public interface NotificationService {

	void saveCommentCreatedNotification(CommentCreatedEvent event);

	void saveReviewLikedNotification(ReviewLikedEvent event);

	void savePopularReviewSelectedNotification(PopularReviewSelectedEvent event);

	void savePopularReviewSelectedNotifications(List<UUID> reviewIds, String period);

	NotificationResponse markNotificationAsRead(
		UUID notificationId,
		UUID requestUserId
	);

	void markAllNotificationsAsRead(UUID userId);

	CursorPageResponse<NotificationResponse> findByUserId(
		UUID userId,
		Direction direction,
		String cursor,
		Instant after,
		int limit
	);
}
