package com.codeit.deokhugam.domain.notification.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.notification.dto.NotificationResponse;
import com.codeit.deokhugam.domain.notification.entity.Notification;
import com.codeit.deokhugam.domain.notification.event.CommentCreatedEvent;
import com.codeit.deokhugam.domain.notification.event.PopularReviewSelectedEvent;
import com.codeit.deokhugam.domain.notification.event.ReviewLikedEvent;
import com.codeit.deokhugam.domain.notification.exception.NotificationNotFoundException;
import com.codeit.deokhugam.domain.notification.exception.NotificationNotOwnedException;
import com.codeit.deokhugam.domain.notification.mapper.NotificationMapper;
import com.codeit.deokhugam.domain.notification.repository.NotificationRepository;
import com.codeit.deokhugam.domain.notification.service.NotificationService;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.review.repository.ReviewRepository;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final NotificationRepository notificationRepository;
	private final NotificationMapper notificationMapper;

	//  TODO: review와 User의 예외 처리는 나중에 통합을 하는 단계에서 예외를 받아서 사용하도록 수정.
	//  알림 생성 메서드는 알림을 생성하는 서비스에서 검증 후 결과로 이벤트 생성을 한다.
	//  그렇기에 알림 생성 메서드는 내부에서 추가 검증 보다는 조회가 정상인지 체크 진행.
	@Override
	@Transactional
	public void saveCommentCreatedNotification(CommentCreatedEvent event) {
		Review review = reviewRepository.findById(event.reviewId())
			.orElseThrow(() -> new RuntimeException("review not found"));

		User receiver = review.getUser();

		User commentedUser = userRepository.findById(event.commentedUserId())
			.orElseThrow(() -> new RuntimeException("commented user not found"));

		String message = String.format(
			"[%s]님이 나의 리뷰에 댓글을 남겼습니다.",
			commentedUser.getNickname()
		);

		saveNotification(receiver, review, message);
	}

	@Override
	@Transactional
	public void saveReviewLikedNotification(ReviewLikedEvent event) {
		Review review = reviewRepository.findById(event.reviewId())
			.orElseThrow(() -> new RuntimeException("review not found"));

		User receiver = review.getUser();

		User likedByUser = userRepository.findById(event.likedByUserId())
			.orElseThrow(() -> new RuntimeException("liked user not found"));

		String message = String.format(
			"[%s]님이 나의 리뷰를 좋아합니다.",
			likedByUser.getNickname()
		);

		saveNotification(receiver, review, message);
	}

	@Override
	@Transactional
	public void savePopularReviewSelectedNotification(PopularReviewSelectedEvent event) {
		Review review = reviewRepository.findById(event.reviewId())
			.orElseThrow(() -> new RuntimeException("review not found"));

		User receiver = review.getUser();

		String message = String.format(
			"[%s]님의 리뷰가 [%s] 인기 리뷰에 선정되었습니다.",
			receiver.getNickname(),
			event.period().getDescription()
		);

		saveNotification(receiver, review, message);
	}

	private void saveNotification(User receiver, Review review, String message) {
		Notification notification = Notification.builder()
			.user(receiver)
			.review(review)
			.message(message)
			.build();

		notificationRepository.save(notification);
	}

	@Override
	@Transactional
	public NotificationResponse markNotificationAsRead(UUID notificationId, UUID requestUserId) {
		//    403도 사용하기 위해 findByIdAndUserId로 통합 조회를 사용하지 않는다.
		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> NotificationNotFoundException.withNotificationId(notificationId));

		if (!Objects.equals(requestUserId, notification.getUser().getId())) {
			throw NotificationNotOwnedException.withRequestedUserId(requestUserId);
		}

		notification.confirm();

		return notificationMapper.toResponse(notification);
	}

	@Override
	@Transactional
	public void markAllNotificationsAsRead(UUID userId) {
		notificationRepository.markAllNotificationsAsRead(userId, Instant.now());
	}

	//  알림 도메인에서 조회시 after는 사용되지 않고 있음(조회 조건이 생성 시간뿐)
	@Override
	@Transactional(readOnly = true)
	public CursorPageResponse<NotificationResponse> findByUserId(
		UUID userId,
		Direction direction,
		String cursor,
		Instant after,
		int limit
	) {
		Instant cursorInstant = cursor == null ? null : Instant.parse(cursor);

		Direction sortDirection = direction == null ? Direction.DESC : direction;

		Pageable pageable = PageRequest.of(
			0,
			limit + 1,
			Sort.by(sortDirection, "createdAt")
		);

		List<Notification> notificationList = sortDirection == Direction.DESC
			? notificationRepository.findAllByUserIdWithCursorDESC(userId, cursorInstant, pageable)
			: notificationRepository.findAllByUserIdWithCursorASC(userId, cursorInstant, pageable);

		//    기존 noti.size == limit 으로 체크.
		//    현재는 limit+1으로 1개 더 불러서 limit보다 많은 경우 다음이 존재한다고 판정
		boolean hasNext = notificationList.size() > limit;

		List<Notification> pageContent = hasNext
			? notificationList.subList(0, limit)
			: notificationList;

		List<NotificationResponse> content = pageContent.stream()
			.map(notificationMapper::toResponse)
			.toList();

		//    hasNext만으로 충분, limit부분은 컨트롤러에서 @Min으로 최소값 보정할 예정
		//    MethodArgumentNotValidException 예외로 처리
		String nextCursor = hasNext
			? pageContent.get(pageContent.size() - 1).getCreatedAt().toString()
			: null;

		long totalElements = notificationRepository.countByUserId(userId);

		return CursorPageResponse.<NotificationResponse>builder()
			.content(content)
			.nextCursor(nextCursor)
			.nextAfter(nextCursor == null ? null : Instant.parse(nextCursor))
			.size(content.size())
			.totalElements(totalElements)
			.hasNext(hasNext)
			.build();
	}
}
