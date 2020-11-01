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
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ChargeServiceImpl implements ChargeService {

    @Autowired
    private ChargeHistoryRepository chargeRepository;

//    @Autowired
//    public ChargeServiceImpl(ChargeHistoryRepository chargeRepository) {
//        this.chargeRepository = chargeRepository;
//    }

    @Override
    public ChargeResponseDTO processCharge(ChargeRequestDTO chargeRequest) {
        ChargeHistoryItem saved = chargeRepository.save(chargeRequest.toChargeHistoryItem());
        return new ChargeResponseDTO(saved.getId(), saved.getDatetime());
    }

    @Override
    public List<HistoryResponseDTO> getBalanceHistory(HistoryRequestDTO historyRequest) {
        ZonedDateTime startTime = historyRequest.getStartDatetime().truncatedTo(ChronoUnit.HOURS).plusHours(1);
        ZonedDateTime endTime = historyRequest.getEndDatetime().truncatedTo(ChronoUnit.HOURS).plusHours(2);
        log.info("Getting balance history from {} to {}, for every hour", startTime, endTime);
        List<HistoryResponseDTO> history = new LinkedList<>();

        while (startTime.isBefore(endTime)) {
            Float quantity = Optional.ofNullable(chargeRepository.getQuantityForDate(startTime)).orElse((float) 0);
            log.info("For time {}, quantity is {}", startTime, quantity);

            HistoryResponseDTO item = new HistoryResponseDTO();
            item.setAmount(quantity);
            item.setDateTime(startTime);
            history.add(item);

            startTime = startTime.plusHours(1);
            log.info("Getting history for next hour {}", startTime);
        }
        if (startTime.equals(endTime)) {

        }

        return history;
    }

    @Override
    public List<ChargeHistoryItem> getAllChargeItems() {
        return chargeRepository.findAll();
    }
}
