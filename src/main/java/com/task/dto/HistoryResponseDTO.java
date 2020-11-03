package com.task.dto;

import lombok.Data;
import java.time.ZonedDateTime;

@Data
public class HistoryResponseDTO implements Comparable<HistoryResponseDTO> {

    public HistoryResponseDTO(){}

    private Float amount;

    private ZonedDateTime dateTime;

    public HistoryResponseDTO(ZonedDateTime dateTime, Float amount){
        this.amount = amount;
        this.dateTime = dateTime;
    }

    @Override
    public int compareTo(HistoryResponseDTO o) {
        return this.getDateTime().compareTo(o.getDateTime());
    }
}
