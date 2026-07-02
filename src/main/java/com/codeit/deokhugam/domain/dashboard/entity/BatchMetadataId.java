package com.codeit.deokhugam.domain.dashboard.entity;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class BatchMetadataId implements Serializable {

	private static final long serialVersionUID = 1L;

	private BatchMetadataType metadataType;
	private PeriodType period;

}
