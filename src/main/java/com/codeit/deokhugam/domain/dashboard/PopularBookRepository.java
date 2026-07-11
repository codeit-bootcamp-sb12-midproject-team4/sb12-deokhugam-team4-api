package com.codeit.deokhugam.domain.dashboard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;
import com.codeit.deokhugam.domain.dashboard.entity.PopularBook;

public interface PopularBookRepository extends JpaRepository<PopularBook, UUID> {

	Optional<PopularBook> findFirstByPeriodOrderByDatasetIdDesc(PeriodType period);

	@Query("SELECT p FROM PopularBook p " +
		"WHERE p.datasetId = :datasetId AND p.ranking > :cursorRanking " +
		"ORDER BY p.ranking ASC")
	List<PopularBook> findPopularBooksByCursor(
		@Param("datasetId") Long datasetId,
		@Param("cursorRanking") Integer cursorRanking,
		Pageable pageable
	);

	long countByDatasetId(Long datasetId);

}
