package com.codeit.deokhugam.domain.comment.entity;

import com.codeit.deokhugam.domain.comment.Comment;
import com.codeit.deokhugam.domain.common.BaseEntity;
import com.codeit.deokhugam.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "reason", length = 500)
    private String reason;

    public CommentReport(Comment comment, User user, String reason) {
        this.comment = comment;
        this.user = user;
        this.reason = reason;
    }
}