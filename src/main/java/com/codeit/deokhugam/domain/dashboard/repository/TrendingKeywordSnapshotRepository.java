package com.codeit.deokhugam.domain.dashboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeywordSnapshot;

public interface TrendingKeywordSnapshotRepository extends JpaRepository<TrendingKeywordSnapshot, Long> {

    // [조회] 가장 최근에 집계된 실시간 트렌드 스냅샷 1건 조회
    Optional<TrendingKeywordSnapshot> findFirstByOrderByCalculatedAtDesc();
}
