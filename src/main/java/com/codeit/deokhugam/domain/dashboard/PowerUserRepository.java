package com.codeit.deokhugam.domain.dashboard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;
import com.codeit.deokhugam.domain.dashboard.entity.PowerUser;

public interface PowerUserRepository extends JpaRepository<PowerUser, UUID> {

	Optional<PowerUser> findFirstByPeriodOrderByDatasetIdDesc(PeriodType period);

	// 2. 커서(ranking) 기반 리스트 조회 (순수 JPA @Query)
	@Query("SELECT p FROM PowerUser p " +
		"WHERE p.datasetId = :datasetId AND p.ranking > :cursorRanking " +
		"ORDER BY p.ranking ASC")
	List<PowerUser> findPowerUsersByCursor(
		@Param("datasetId") Long datasetId,
		@Param("cursorRanking") Integer cursorRanking,
		Pageable pageable
	);

	// 3. 전체 개수 조회
	long countByDatasetId(Long datasetId);

}
