package com.codeit.deokhugam.domain.comment.repository;

import com.codeit.deokhugam.domain.comment.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, UUID> {

    boolean existsByCommentIdAndUserId(UUID commentId, UUID userId);
}