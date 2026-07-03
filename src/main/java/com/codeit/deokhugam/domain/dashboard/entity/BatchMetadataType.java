package com.codeit.deokhugam.domain.dashboard.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BatchMetadataType {

	POPULAR_BOOK("POPULAR_BOOK"),
	POPULAR_REVIEW("POPULAR_REVIEW"),
	POWER_USER("POWER_USER"),
	TRENDING_KEYWORD("TRENDING_KEYWORD");

	private final String code;

}