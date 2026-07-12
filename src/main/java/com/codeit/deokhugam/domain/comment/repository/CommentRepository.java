package com.codeit.deokhugam.domain.comment.repository;

import com.codeit.deokhugam.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByReviewIdAndDeletedAtIsNull(UUID reviewId);

    List<Comment> findByUserIdAndDeletedAtIsNull(UUID userId);
}