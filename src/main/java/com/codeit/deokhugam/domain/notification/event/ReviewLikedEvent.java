package com.codeit.deokhugam.domain.notification.event;

import java.util.UUID;

public record ReviewLikedEvent(
    UUID reviewId,
    UUID likedByUserId
) {

}
