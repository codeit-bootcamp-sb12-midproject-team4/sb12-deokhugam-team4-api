package com.codeit.deokhugam.domain.notification.exception;

import java.util.UUID;

import com.codeit.deokhugam.global.exception.ErrorCode;

public class NotificationNotFoundException extends NotificationException {

	public NotificationNotFoundException() {
		super(ErrorCode.NOTIFICATION_NOT_FOUND);
	}

	public static NotificationNotFoundException withNotificationId(UUID notificationId) {
		NotificationNotFoundException exception = new NotificationNotFoundException();
		exception.addDetail("notificationId", notificationId);
		return exception;
	}
}
