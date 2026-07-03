package com.codeit.deokhugam.domain.comment.controller;

import com.codeit.deokhugam.domain.comment.dto.CommentCreateRequest;
import com.codeit.deokhugam.domain.comment.dto.CommentResponse;
import com.codeit.deokhugam.domain.comment.dto.CommentUpdateRequest;
import com.codeit.deokhugam.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Valid @RequestBody CommentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(request));
    }

    // 댓글 목록 조회
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @RequestParam UUID reviewId) {
        return ResponseEntity.ok(commentService.getComments(reviewId));
    }

    // 댓글 단건 조회
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getComment(
            @PathVariable UUID commentId) {
        return ResponseEntity.ok(commentService.getComment(commentId));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId,
            @Valid @RequestBody CommentUpdateRequest request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, userId, request));
    }

    // 댓글 논리 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    // 댓글 물리 삭제
    @DeleteMapping("/{commentId}/hard")
    public ResponseEntity<Void> hardDeleteComment(
            @PathVariable UUID commentId) {
        commentService.hardDeleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}