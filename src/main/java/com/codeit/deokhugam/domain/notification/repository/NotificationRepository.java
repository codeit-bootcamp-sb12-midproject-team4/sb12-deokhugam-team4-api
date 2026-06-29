package com.codeit.deokhugam.domain.notification.repository;

import com.codeit.deokhugam.domain.notification.entity.Notification;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  //  단건 조회용
  Optional<Notification> findByIdAndUserId(UUID id, UUID userId);

  //  전체 수정용. 전체 조회후 업데이트가 아닌 벌크 업데이트로 쿼리 속도 향상.
  //  하지만 자동으로 updateAt이 업데이트가 되지 않기 떄문에 Instant를 받아서 사용
  @Modifying
  @Query(
      """
            UPDATE Notification n
            SET n.confirmed = true,
              n.updatedAt = :now
            WHERE n.user.id = :userId
              and n.confirmed = false
          """)
  void markAllNotificationsAsRead(UUID userId, Instant now);

  // 정렬 방향은 서비스에서 Pageable Sort로 전달한다.
  @Query("""
      select n
      from Notification n
      join fetch n.user
      join fetch n.review r
      where n.user.id = :userId
        and (:cursor is null or n.createdAt < :cursor)
      """)
  List<Notification> findAllByUserIdWithCursorDESC(
      UUID userId,
      Instant cursor,
      Pageable pageable
  );

  @Query("""
      select n
      from Notification n
      join fetch n.user
      join fetch n.review r
      where n.user.id = :userId
        and (:cursor is null or n.createdAt > :cursor)
      """)
  List<Notification> findAllByUserIdWithCursorASC(
      UUID userId,
      Instant cursor,
      Pageable pageable
  );

  //  threshold = now - 7days
  @Modifying
  @Query("""
      delete from Notification n
      where n.confirmed = true
        and n.updatedAt < :threshold
      """)
  int deleteConfirmedAfter(Instant threshold);
}
