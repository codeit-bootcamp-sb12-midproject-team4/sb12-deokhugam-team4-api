package com.codeit.deokhugam.domain.notification.event;

import java.util.UUID;

public record CommentCreatedEvent(
    UUID reviewId,
    UUID commentedUserId
) {

}
