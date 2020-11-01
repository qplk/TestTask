package com.task.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
public class HistoryRequestDTO {

    private ZonedDateTime startDatetime;

    private ZonedDateTime endDatetime;
}
