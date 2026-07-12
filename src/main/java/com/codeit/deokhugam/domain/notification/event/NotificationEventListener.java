package com.codeit.deokhugam.domain.notification.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.codeit.deokhugam.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

	private final NotificationService notificationService;

	//  로그는 생략
	// requires_new는 리스너가 after_commit이기 때문에 db 저장을 위해 추가.
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(CommentCreatedEvent event) {
		notificationService.saveCommentCreatedNotification(event);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(ReviewLikedEvent event) {
		notificationService.saveReviewLikedNotification(event);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(PopularReviewSelectedEvent event) {
		notificationService.savePopularReviewSelectedNotification(event);
	}
}
