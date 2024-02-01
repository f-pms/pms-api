package com.hbc.pms.integration.db.repository;

import com.hbc.pms.core.model.enums.AlarmStatus;
import com.hbc.pms.integration.db.entity.AlarmHistoryEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface AlarmHistoryRepository extends CrudRepository<AlarmHistoryEntity, Long> {
    @Query(
        value = "SELECT * FROM alarm_history WHERE status <> 'SOLVED' AND alarm_condition_id = :alarm_condition_id",
        nativeQuery = true
    )
    Optional<AlarmHistoryEntity> findUnsolvedByConditionId(@Param("alarm_condition_id") Long id);

    List<AlarmHistoryEntity> findAllByStatus(AlarmStatus status);
}