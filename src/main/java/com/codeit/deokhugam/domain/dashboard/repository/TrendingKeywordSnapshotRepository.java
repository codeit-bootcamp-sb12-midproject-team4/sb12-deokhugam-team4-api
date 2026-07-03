package com.codeit.deokhugam.domain.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeywordSnapshot;

public interface TrendingKeywordSnapshotRepository
    extends JpaRepository<TrendingKeywordSnapshot, Long> {
}