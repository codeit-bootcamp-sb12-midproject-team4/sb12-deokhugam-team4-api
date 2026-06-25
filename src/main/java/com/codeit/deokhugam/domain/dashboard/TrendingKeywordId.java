package com.codeit.deokhugam.domain.dashboard;

import java.io.Serializable;
import java.util.Objects;

public class TrendingKeywordId implements Serializable {
	private Long snapshot; // TrendingKeyword 엔티티의 필드명과 일치해야 함
	private int ranking;

	public TrendingKeywordId() {}

	public TrendingKeywordId(Long snapshot, int ranking) {
		this.snapshot = snapshot;
		this.ranking = ranking;
	}

	// equals & hashCode 구현 필수
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TrendingKeywordId that = (TrendingKeywordId) o;
		return ranking == that.ranking && Objects.equals(snapshot, that.snapshot);
	}

	@Override
	public int hashCode() {
		return Objects.hash(snapshot, ranking);
	}
}
