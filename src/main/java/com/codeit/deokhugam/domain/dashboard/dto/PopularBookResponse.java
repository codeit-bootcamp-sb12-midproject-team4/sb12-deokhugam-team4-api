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
public class PopularBookResponse {
	private UUID id;
	private UUID bookId;
	private String title;
	private String author;
	private String thumbnailUrl;
	private PeriodType period;
	private Integer rank;
	private Double score;
	private Long reviewCount;
	private Double rating;
	private Instant createdAt;
}
