package com.codeit.deokhugam.domain.client.isbn;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverApiResponse {
	private List<NaverApiResponseItem> items;
}
