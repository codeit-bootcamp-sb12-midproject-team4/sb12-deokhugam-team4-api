package com.codeit.deokhugam.domain.dashboard.repository;

import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;
import com.codeit.deokhugam.domain.dashboard.entity.PopularReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PopularReviewRepository extends JpaRepository<PopularReview, UUID> {

    List<PopularReview> findByPeriodAndBatchDateOrderByRankingAsc(
        PeriodType period,
        LocalDate batchDate
    );

    List<PopularReview> findByDatasetIdOrderByRankingAsc(Long datasetId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM PopularReview r
            WHERE r.period = :period
              AND r.batchDate = :batchDate
            """)
    long deleteByPeriodAndBatchDate(
        PeriodType period,
        LocalDate batchDate
    );
}