package com.codeit.deokhugam.domain.dashboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeit.deokhugam.domain.dashboard.entity.BatchMetadata;
import com.codeit.deokhugam.domain.dashboard.entity.BatchMetadataId;
import com.codeit.deokhugam.domain.dashboard.entity.BatchMetadataType;
import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;

public interface BatchMetadataRepository
	extends JpaRepository<BatchMetadata, BatchMetadataId> {

	 // 메타데이터 타입과 기간에 해당하는 현재 배치 메타데이터 조회
	Optional<BatchMetadata> findByMetadataTypeAndPeriod(
		BatchMetadataType metadataType,
		PeriodType period
	);
}
