package com.saf.userservice.repository;

import com.saf.userservice.model.AdminAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminActionRepository extends JpaRepository<AdminAction, Long> {

    List<AdminAction> findByAdminUsernameOrderByCreatedAtDesc(String adminUsername);

    List<AdminAction> findByActionTypeOrderByCreatedAtDesc(String actionType);

    List<AdminAction> findByTargetTypeOrderByCreatedAtDesc(String targetType);

    List<AdminAction> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(String targetType, Long targetId);

    List<AdminAction> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);

    List<AdminAction> findAllByOrderByCreatedAtDesc();
}
