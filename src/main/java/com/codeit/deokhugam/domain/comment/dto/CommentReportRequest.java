package com.codeit.deokhugam.domain.comment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CommentReportRequest(

        @NotNull(message = "사용자 ID는 필수입니다")
        UUID userId,

        @Size(max = 500, message = "신고 사유는 500자 이하여야 합니다")
        String reason
) {}