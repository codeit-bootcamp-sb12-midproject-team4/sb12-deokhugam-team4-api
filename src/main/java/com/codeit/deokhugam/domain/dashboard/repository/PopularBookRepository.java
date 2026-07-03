package com.codeit.deokhugam.domain.dashboard.repository;

import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;
import com.codeit.deokhugam.domain.dashboard.entity.PopularBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PopularBookRepository extends JpaRepository<PopularBook, UUID> {

    List<PopularBook> findByPeriodAndBatchDateOrderByRankingAsc(
        PeriodType period,
        LocalDate batchDate
    );

    List<PopularBook> findByDatasetIdOrderByRankingAsc(Long datasetId);

    //배치 데이터 정리
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM PopularBook p
            WHERE p.period = :period
              AND p.batchDate = :batchDate
            """)
    long deleteByPeriodAndBatchDate(
        PeriodType period,
        LocalDate batchDate
    );
}