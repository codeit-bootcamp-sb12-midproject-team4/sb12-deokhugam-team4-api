package com.codeit.deokhugam.domain.dashboard.dto;

import java.time.Instant;
import java.util.UUID;

import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PowerUserResponse {
	private UUID userId;
	private String nickname;
	private PeriodType period;
	private Instant createdAt;
	private Integer rank;
	private Double score;
	private Double reviewScoreSum;
	private Long likeCount;
	private Long commentCount;
}
