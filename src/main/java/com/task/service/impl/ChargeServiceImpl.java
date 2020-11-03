package com.task.service.impl;

import com.task.dto.ChargeRequestDTO;
import com.task.dto.ChargeResponseDTO;
import com.task.dto.HistoryRequestDTO;
import com.task.dto.HistoryResponseDTO;
import com.task.model.ChargeHistoryItem;
import com.task.repository.ChargeHistoryRepository;
import com.task.service.ChargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
public class ChargeServiceImpl implements ChargeService {

    @Autowired
    private ChargeHistoryRepository chargeRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public ChargeResponseDTO processCharge(ChargeRequestDTO chargeRequest) {
        ChargeHistoryItem saved = chargeRepository.save(chargeRequest.toChargeHistoryItem());
        return new ChargeResponseDTO(saved.getId(), saved.getDatetime());
    }

    @Override
    public List<HistoryResponseDTO> getBalanceHistory(HistoryRequestDTO historyRequest) {
        roundRequest(historyRequest);
        ZonedDateTime startTime = ZonedDateTime.ofInstant(historyRequest.getStartDatetime().toInstant(), ZoneId.systemDefault());
        ZonedDateTime endTime = ZonedDateTime.ofInstant(historyRequest.getEndDatetime().toInstant(), ZoneId.systemDefault());
        log.info("Getting balance history from {} to {}, for every hour", startTime, endTime);
        List<HistoryResponseDTO> history = new LinkedList<>();

        Float quantityUntilStartTime = Optional.ofNullable(chargeRepository.getQuantityUntilStartTime(startTime)).orElse((float) 0.0);
        List<HistoryResponseDTO> quantityHourly = getQuantityHourly(startTime, endTime);
        Map<ZonedDateTime, Float> map = new LinkedHashMap<>();
        quantityHourly.forEach(value -> map.put(value.getDateTime(), value.getAmount()));

        map.put(startTime, Optional.ofNullable(map.get(startTime)).orElse((float) 0.0) + quantityUntilStartTime);

        while (startTime.isBefore(endTime.plusHours(1))) {
            if (map.containsKey(startTime)) {
                map.put(startTime, map.get(startTime) + Optional.ofNullable(map.get(startTime.minusHours(1))).orElse((float) 0.0));
            } else {
                map.put(startTime, map.get(startTime.minusHours(1)));
            }
            startTime = startTime.plusHours(1);
        }

        map.forEach((key, value) -> history.add(new HistoryResponseDTO(key, value)));
        Collections.sort(history);

        return history;
    }

    @Override
    public List<ChargeHistoryItem> getAllChargeItems() {
        return chargeRepository.findAll();
    }

    private void roundRequest(HistoryRequestDTO request) {
        ZonedDateTime startTime = request.getStartDatetime();
        ZonedDateTime endTime = request.getEndDatetime();

        if (startTime.getMinute() >= 0 && startTime.getMinute() < 30) {
            request.setStartDatetime(startTime.truncatedTo(ChronoUnit.HOURS));
        } else {
            request.setStartDatetime(startTime.truncatedTo(ChronoUnit.HOURS).plusHours(1));
        }

        if (endTime.getMinute() >= 0 && endTime.getMinute() < 30) {
            request.setEndDatetime(endTime.truncatedTo(ChronoUnit.HOURS));
        } else {
            request.setEndDatetime(endTime.truncatedTo(ChronoUnit.HOURS).plusHours(1));
        }
    }

    private List<HistoryResponseDTO> getQuantityHourly(ZonedDateTime startTime, ZonedDateTime endTime) {
        String sql = "select PARSEDATETIME(concat(YEAR(datetime), '-',MONTH(datetime), '-',DAY_OF_MONTH(datetime), ' ', HOUR(datetime), ':00:00'), 'yyyy-MM-dd HH:mm:ss') as datetime, sum(amount) as amount\n" +
                "from CHARGE_HISTORY\n" +
                "where datetime between :startTime and :endTime \n" +
                "group by datetime";

        Map<String, Object> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);

        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            HistoryResponseDTO historyDto = new HistoryResponseDTO();
            ZonedDateTime datetime = ZonedDateTime.ofInstant(rs.getTimestamp("datetime").toInstant(), ZoneId.systemDefault());
            historyDto.setAmount(rs.getFloat("amount"));
            historyDto.setDateTime(datetime);
            return historyDto;
        });
    }
}
