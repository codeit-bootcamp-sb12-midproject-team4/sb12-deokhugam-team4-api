package com.codeit.deokhugam.domain.notification.exception;

import java.util.UUID;

import com.codeit.deokhugam.global.exception.ErrorCode;

public class NotificationNotOwnedException extends NotificationException {

	public NotificationNotOwnedException() {
		super(ErrorCode.NOTIFICATION_NOT_OWNED);
	}

	public static NotificationNotOwnedException withRequestedUserId(UUID requestedUserId) {
		NotificationNotOwnedException exception = new NotificationNotOwnedException();
		exception.addDetail("requestedUserId", requestedUserId);
		return exception;
	}
}
