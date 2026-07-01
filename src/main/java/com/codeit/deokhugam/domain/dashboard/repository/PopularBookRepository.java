package com.codeit.deokhugam.domain.dashboard.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;
import com.codeit.deokhugam.domain.dashboard.entity.PopularBook;

public interface PopularBookRepository extends JpaRepository<PopularBook, UUID> {

    // [조회] 대시보드 랭킹 화면 출력 전용
    List<PopularBook> findByPeriodAndBatchDateOrderByRankingAsc(PeriodType period, LocalDate batchDate);

    // [배치] JPQL 없이 Spring Data JPA 컨벤션으로 처리하는 벌크 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    long deleteByPeriodAndBatchDate(PeriodType period, LocalDate batchDate);
}
