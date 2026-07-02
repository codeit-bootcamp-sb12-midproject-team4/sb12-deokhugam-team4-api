package com.codeit.deokhugam.domain.dashboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeyword;
import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeywordId;

public interface TrendingKeywordRepository extends JpaRepository<TrendingKeyword, TrendingKeywordId> {

    List<TrendingKeyword> findBySnapshot_DatasetIdOrderByRankingAsc(Long datasetId);
}
