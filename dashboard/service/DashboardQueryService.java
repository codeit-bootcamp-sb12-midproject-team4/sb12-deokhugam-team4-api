package com.codeit.deokhugam.domain.dashboard.service;

import static com.codeit.deokhugam.global.exception.ErrorCode.DATASET_NOT_FOUND;

import com.codeit.deokhugam.domain.dashboard.entity.BatchMetadata;
import com.codeit.deokhugam.domain.dashboard.entity.BatchMetadataType;
import com.codeit.deokhugam.domain.dashboard.entity.PeriodType;
import com.codeit.deokhugam.domain.dashboard.exception.DashboardException;
import com.codeit.deokhugam.domain.dashboard.repository.BatchMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardQueryService {

	private final BatchMetadataRepository batchMetadataRepository;

	public Long getDatasetId(
		BatchMetadataType metadataType,
		PeriodType period
	) {
		return batchMetadataRepository
			.findByMetadataTypeAndPeriod(metadataType, period)
			.map(BatchMetadata::getDatasetId)
			.orElseThrow(() ->
				new DashboardException(DATASET_NOT_FOUND)
			);
	}
}