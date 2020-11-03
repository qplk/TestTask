package com.task.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "CHARGE_HISTORY")
@Data
public class ChargeHistoryItem extends BaseEntity {

    public ChargeHistoryItem(){}

    public ChargeHistoryItem(Float amount, ZonedDateTime dateTime){
        this.amount = amount;
        this.datetime = dateTime;
    }

    @Column
    private Float amount;

    @Column
    private ZonedDateTime datetime;
}
