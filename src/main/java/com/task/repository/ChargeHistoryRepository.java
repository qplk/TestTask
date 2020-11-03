package com.task.repository;

import com.task.dto.HistoryResponseDTO;
import com.task.model.ChargeHistoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ChargeHistoryRepository extends JpaRepository<ChargeHistoryItem, Long> {

    @Query(value = "select sum(amount) from CHARGE_HISTORY where datetime <= ?1", nativeQuery = true)
    Float getQuantityForDate(ZonedDateTime dateTime);

    @Query(value = "select sum(amount) from CHARGE_HISTORY where datetime < ?1", nativeQuery = true)
    Float getQuantityUntilStartTime(ZonedDateTime startTime);
}
