package com.codeit.deokhugam.domain.comment.service;

import com.codeit.deokhugam.domain.comment.Comment;
import com.codeit.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.codeit.deokhugam.domain.comment.dto.CommentResponse;
import com.codeit.deokhugam.domain.comment.dto.CommentUpdateRequest;
import com.codeit.deokhugam.domain.comment.exception.CommentNotFoundException;
import com.codeit.deokhugam.domain.comment.exception.CommentNotOwnedException;
import com.codeit.deokhugam.domain.comment.repository.CommentRepository;
import com.codeit.deokhugam.domain.notification.event.CommentCreatedEvent;
import com.codeit.deokhugam.domain.review.entity.Review;
import com.codeit.deokhugam.domain.review.repository.ReviewRepository;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;

    // 댓글 등록
    @Transactional
    public CommentResponse createComment(CommentCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(CommentNotFoundException::new);
        Review review = reviewRepository.findById(request.reviewId())
                .orElseThrow(CommentNotFoundException::new);

        Comment comment = new Comment(request.content(), user, review);
        Comment savedComment = commentRepository.save(comment);

        eventPublisher.publishEvent(new CommentCreatedEvent(
            review.getId(),
            user.getId()
        ));

        return CommentResponse.from(savedComment);
    }

    // 댓글 목록 조회
    public List<CommentResponse> getComments(UUID reviewId) {
        return commentRepository.findByReviewIdAndDeletedAtIsNull(reviewId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    // 댓글 단건 조회
    public CommentResponse getComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        if (comment.isDeleted()) {
            throw new CommentNotFoundException();
        }
        return CommentResponse.from(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(UUID commentId, UUID userId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        if (comment.isDeleted()) {
            throw new CommentNotFoundException();
        }
        if (!comment.getUser().getId().equals(userId)) {
            throw new CommentNotOwnedException();
        }
        return CommentResponse.from(comment);
    }

    // 댓글 논리 삭제
    @Transactional
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        if (comment.isDeleted()) {
            throw new CommentNotFoundException();
        }
        if (!comment.getUser().getId().equals(userId)) {
            throw new CommentNotOwnedException();
        }
        comment.markDeleted();
    }

    // 댓글 물리 삭제
    @Transactional
    public void hardDeleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
        commentRepository.delete(comment);
    }
}