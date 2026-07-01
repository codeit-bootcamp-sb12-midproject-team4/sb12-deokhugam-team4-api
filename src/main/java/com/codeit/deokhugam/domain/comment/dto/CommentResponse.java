package com.codeit.deokhugam.domain.comment.dto;

import com.codeit.deokhugam.domain.comment.Comment;
import java.time.Instant;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID reviewId,
        UUID userId,
        String userNickname,
        String content,
        Instant createdAt,
        Instant updatedAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getReview().getId(),
                comment.getUser().getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}