package com.task.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.task.model.ChargeHistoryItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
public class ChargeRequestDTO {

    private Float amount;
    private ZonedDateTime datetime;

    public ChargeHistoryItem toChargeHistoryItem() {
        ChargeHistoryItem chargeHistoryItem = new ChargeHistoryItem();
        chargeHistoryItem.setAmount(amount);
        chargeHistoryItem.setDatetime(datetime);
        return chargeHistoryItem;
    }
}
