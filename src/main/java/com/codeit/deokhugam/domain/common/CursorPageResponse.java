package com.codeit.deokhugam.domain.common;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageResponse<T> {
	private List<T> content;
	private String nextCursor;
	private Instant nextAfter;
	private int size;
	private long totalElements;
	private boolean hasNext;
}
