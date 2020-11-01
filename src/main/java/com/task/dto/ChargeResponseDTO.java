package com.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class ChargeResponseDTO {

    public ChargeResponseDTO(){}

    private Long transId;
    private ZonedDateTime transTime;
}
