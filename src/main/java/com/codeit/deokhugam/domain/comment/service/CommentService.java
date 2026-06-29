package com.codeit.deokhugam.domain.comment.service;

import com.codeit.deokhugam.domain.comment.Comment;
import com.codeit.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.codeit.deokhugam.domain.comment.dto.CommentResponse;
import com.codeit.deokhugam.domain.comment.dto.CommentUpdateRequest;
import com.codeit.deokhugam.domain.comment.repository.CommentRepository;
import com.codeit.deokhugam.domain.review.Review;
import com.codeit.deokhugam.domain.review.repository.ReviewRepository;
import com.codeit.deokhugam.domain.user.User;
import com.codeit.deokhugam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    // 댓글 등록
    @Transactional
    public CommentResponse createComment(CommentCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        Review review = reviewRepository.findById(request.reviewId())
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다"));

        Comment comment = new Comment(request.content(), user, review);
        return CommentResponse.from(commentRepository.save(comment));
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
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다"));
        return CommentResponse.from(comment);
    }

    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(UUID commentId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다"));
        return CommentResponse.from(comment);
    }

    // 댓글 논리 삭제
    @Transactional
    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다"));
        comment.markDeleted();
    }
}