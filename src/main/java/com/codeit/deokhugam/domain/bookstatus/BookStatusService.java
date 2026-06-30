package com.codeit.deokhugam.domain.bookstatus;

import java.util.UUID;

public interface BookStatusService {

	void putStatus(UUID bookId, UUID userId, BookStatusType type);

	void deleteStatus(UUID bookId, UUID userId);

}
