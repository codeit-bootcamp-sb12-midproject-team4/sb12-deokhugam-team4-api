package com.codeit.deokhugam.domain.bookstatus;

import java.util.UUID;

public interface BookStatusService {

	void save(UUID bookId, BookStatusType type);

	void delete(UUID bookId);

}
