package com.task.service;

import com.task.dto.ChargeRequestDTO;
import com.task.dto.ChargeResponseDTO;
import com.task.dto.HistoryRequestDTO;
import com.task.dto.HistoryResponseDTO;
import com.task.model.ChargeHistoryItem;

import java.util.List;

public interface ChargeService {

    ChargeResponseDTO processCharge(ChargeRequestDTO chargeRequest);

    List<HistoryResponseDTO> getBalanceHistory(HistoryRequestDTO historyRequest);

    List<ChargeHistoryItem> getAllChargeItems();
}
