package com.codeit.deokhugam.domain.notification.controller;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codeit.deokhugam.domain.common.CursorPageResponse;
import com.codeit.deokhugam.domain.notification.dto.NotificationResponse;
import com.codeit.deokhugam.domain.notification.dto.NotificationUpdateRequest;
import com.codeit.deokhugam.domain.notification.service.NotificationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	@PatchMapping("/{notificationId}")
	public void markNotificationAsRead(
		@PathVariable("notificationId") UUID notificationId,
		@RequestHeader("Deokhugam-Request-User-ID") UUID userId,
		@RequestBody @Valid NotificationUpdateRequest notificationUpdateRequest
	) {
		//    confirmed는 항상 true로만 던지기 때문에 추가 검증을 하지 않고 있음.
		notificationService.markNotificationAsRead(notificationId, userId);
	}

	@PatchMapping("/read-all")
	public void markAllNotificationsAsRead(
		@RequestHeader("Deokhugam-Request-User-ID") UUID userId
	) {
		notificationService.markAllNotificationsAsRead(userId);
	}

	@GetMapping
	public CursorPageResponse<NotificationResponse> getNotifications(
		@RequestHeader("Deokhugam-Request-User-ID") UUID userId,
		@RequestParam(defaultValue = "DESC") Direction direction,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant after,
		@RequestParam(defaultValue = "20") @Min(1) int limit
	) {
		return notificationService.findByUserId(userId, direction, cursor, after, limit);
	}
}
