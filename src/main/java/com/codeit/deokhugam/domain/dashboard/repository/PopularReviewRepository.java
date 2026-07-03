package com.codeit.deokhugam.domain.dashboard.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;
import com.codeit.deokhugam.domain.dashboard.entity.PopularReview;

public interface PopularReviewRepository extends JpaRepository<PopularReview, UUID> {

    List<PopularReview> findByPeriodAndBatchDateOrderByRankingAsc(PeriodType period, LocalDate batchDate);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM PopularReview r WHERE r.period = :period AND r.batchDate = :batchDate")
    long deleteByPeriodAndBatchDate(PeriodType period, LocalDate batchDate);
}