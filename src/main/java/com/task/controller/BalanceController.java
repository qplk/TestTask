package com.task.controller;

import com.task.dto.ChargeRequestDTO;
import com.task.dto.ChargeResponseDTO;
import com.task.dto.HistoryRequestDTO;
import com.task.dto.HistoryResponseDTO;
import com.task.exceptions.RestException;
import com.task.service.ChargeService;
import com.task.utils.ValidationResult;
import com.task.utils.rest.RestErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.task.utils.Validator.validateChargeRequest;
import static com.task.utils.Validator.validateHistoryRequest;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class BalanceController {

    private final ChargeService chargeService;

    @Autowired
    public BalanceController(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    @PostMapping(value = "/charge", produces = "application/json")
    public ResponseEntity<?> charge(@RequestBody ChargeRequestDTO chargeRequest) {
        log.info("Start charge request");

        ValidationResult validationResult = validateChargeRequest(chargeRequest);
        if (!validationResult.isValid()) {
            throw new RestException(HttpStatus.BAD_REQUEST, validationResult.getMessage());
        }

        ChargeResponseDTO response = chargeService.processCharge(chargeRequest);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/history", produces = "application/json")
    public ResponseEntity<?> getAllHistoryItems(@RequestBody HistoryRequestDTO historyRequest) {
        log.info("Getting all charge items");

        ValidationResult validationResult = validateHistoryRequest(historyRequest);
        if (!validationResult.isValid()) {
            throw new RestException(HttpStatus.BAD_REQUEST, validationResult.getMessage());
        }

        List<HistoryResponseDTO> balanceHistory = chargeService.getBalanceHistory(historyRequest);

        return new ResponseEntity<>(balanceHistory, HttpStatus.OK);
    }

    @ExceptionHandler(value = RestException.class)
    public ResponseEntity<RestErrorResponse> handleRestException(RestException ex) {
        return new ResponseEntity<>(new RestErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
