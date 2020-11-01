package com.task;

import com.task.model.ChargeHistoryItem;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;

public class TestUtils {

    public static ChargeHistoryItem buildHistoryItem(float amount, ZonedDateTime time) {
        ChargeHistoryItem chi = new ChargeHistoryItem();
        chi.setAmount(amount);
        chi.setDatetime(time);
        return chi;
    }

    public static String readFileAsString(String file)throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(file)));
    }
}
