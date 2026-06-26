package com.codeit.deokhugam.domain.dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeyword;
import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeywordId;

public interface TrendingKeywordRepository extends JpaRepository<TrendingKeyword, TrendingKeywordId> {

    // [조회] Property Expression(_)을 적용하여 최신 스냅샷 ID에 매핑된 키워드 10개 정렬 조회
    List<TrendingKeyword> findBySnapshot_SnapshotIdOrderByRankingAsc(Long snapshotId);

    // 삭제 메서드는 부모 엔티티(TrendingKeywordSnapshot)의 Cascade 옵션으로 자동 처리되므로 제거함
}
