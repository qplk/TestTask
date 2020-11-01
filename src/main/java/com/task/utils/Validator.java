package com.task.utils;

import com.task.dto.ChargeRequestDTO;
import com.task.dto.HistoryRequestDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@Slf4j
public class Validator {

    public static ValidationResult validateChargeRequest(ChargeRequestDTO chargeRequest) {

        if (chargeRequest == null) {
            log.error("ChargeRequest can not be empty");
            return new ValidationResult(false, "ChargeRequest can not be empty");
        }

        if (chargeRequest.getAmount() == null || chargeRequest.getAmount() <= 0) {
            log.error("Charge amount can not be empty, 0 or less");
            return new ValidationResult(false, "Charge amount can not be empty, 0 or negative");
        }

        if (chargeRequest.getDatetime() == null) {
            log.error("Charge datetime can not be empty");
            return new ValidationResult(false, "Charge datetime can not be empty");
        }

        if (chargeRequest.getDatetime().isAfter(ZonedDateTime.now())){
            log.error("Can not charge in future");
            return new ValidationResult(false, "Can not charge in future");
        }

        return new ValidationResult(true, "OK");
    }

    public static ValidationResult validateHistoryRequest(HistoryRequestDTO historyRequest) {

        if (historyRequest == null) {
            log.error("History request can not be null");
            return new ValidationResult(false, "History request can not be null");
        }

        if (historyRequest.getStartDatetime() == null || historyRequest.getEndDatetime() == null) {
            log.error("History request has empty start or end time");
            return new ValidationResult(false, "History request has empty start or end time");
        }

        if (historyRequest.getStartDatetime().isAfter(historyRequest.getEndDatetime())){
            log.error("Requested start time is {} before end time {}", historyRequest.getStartDatetime(), historyRequest.getEndDatetime());
            return new ValidationResult(false, "Requested start time is before end time");
        }

        if (historyRequest.getStartDatetime().isAfter(ZonedDateTime.now())) {
            log.error("Requested start time {} in future", historyRequest.getStartDatetime());
            return new ValidationResult(false, "Requested start time in future");
        }

        if (historyRequest.getEndDatetime().isAfter(ZonedDateTime.now())) {
            log.debug("Can not predict future, end time replaced by current time");
            historyRequest.setEndDatetime(ZonedDateTime.now());
        }

        return new ValidationResult(true, "OK");
    }
}
