package com.codeit.deokhugam.domain.dashboard.entity;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class TrendingKeywordId implements Serializable {

// 역직렬화 안정성과 경고 제거를 위한 버전 ID 명시
	private static final long serialVersionUID = 1L;

	private Long snapshot;
	private Integer ranking;
}
