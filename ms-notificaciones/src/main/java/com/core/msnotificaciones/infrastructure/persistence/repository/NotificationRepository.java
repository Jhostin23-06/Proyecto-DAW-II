package com.core.msnotificaciones.infrastructure.persistence.repository;

import com.core.msnotificaciones.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>,
        JpaSpecificationExecutor<NotificationEntity> {

    long countByReadFalse();
    long countByReadFalseAndTargetUserId(String targetUserId);
    long countByReadFalseAndTargetUserIdIsNotNull();

    @Modifying
    @Query("update NotificationEntity n set n.read = true, n.readAt = :readAt where n.read = false")
    int markAllAsRead(@Param("readAt") Instant readAt);

    @Modifying
    @Query("""
            update NotificationEntity n
               set n.read = true, n.readAt = :readAt
             where n.read = false
               and n.targetUserId is not null
            """)
    int markAllTargetedAsRead(@Param("readAt") Instant readAt);

    @Modifying
    @Query("""
            update NotificationEntity n
               set n.read = true, n.readAt = :readAt
             where n.read = false
               and n.targetUserId = :targetUserId
            """)
    int markAllAsReadByTargetUserId(@Param("targetUserId") String targetUserId, @Param("readAt") Instant readAt);
}
