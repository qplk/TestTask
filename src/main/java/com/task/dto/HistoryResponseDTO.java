package com.task.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class HistoryResponseDTO {

    private Float amount;

    private ZonedDateTime dateTime;

}
