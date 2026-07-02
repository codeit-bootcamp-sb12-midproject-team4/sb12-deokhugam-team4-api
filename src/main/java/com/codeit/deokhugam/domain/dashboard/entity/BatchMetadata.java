package com.codeit.deokhugam.domain.dashboard.entity;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
	name = "batch_metadata",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_batch_metadata_dataset_id", columnNames = {"dataset_id"})
	}
)
@IdClass(BatchMetadataId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
public class BatchMetadata {

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "metadata_type", nullable = false)
	private BatchMetadataType metadataType;

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "period", nullable = false)
	private PeriodType period;

	@Column(name = "dataset_id", nullable = false)
	private Long datasetId;

	@Column(name = "batch_date")
	private LocalDate batchDate;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

}
