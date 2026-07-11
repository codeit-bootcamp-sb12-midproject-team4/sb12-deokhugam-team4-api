package com.codeit.deokhugam.domain.dashboard;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codeit.deokhugam.domain.dashboard.entity.TrendingKeyword;

public interface TrendingKeywordRepository extends JpaRepository<TrendingKeyword, UUID> {

	List<TrendingKeyword> findTop10BySnapshot_DatasetIdOrderByRankingAsc(Long datasetId);

}
