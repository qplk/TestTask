package com.task.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "CHARGE_HISTORY")
@AllArgsConstructor
@Data
public class ChargeHistoryItem extends BaseEntity {

    public ChargeHistoryItem(){}

    @Column
    private Float amount;

    @Column
    private ZonedDateTime datetime;
}
