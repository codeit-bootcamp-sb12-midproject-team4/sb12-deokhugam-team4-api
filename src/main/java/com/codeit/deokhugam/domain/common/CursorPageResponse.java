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

	public static <T> CursorPageResponse<T> empty(int size) {
		return CursorPageResponse.<T>builder()
			.content(List.of()) // 빈 리스트 보장
			.nextCursor(null)
			.nextAfter(null)
			.size(size)         // 요청받은 사이즈 유지
			.totalElements(0)
			.hasNext(false)
			.build();
	}
}
