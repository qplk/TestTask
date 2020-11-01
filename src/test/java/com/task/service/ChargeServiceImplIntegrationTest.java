package com.task.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.task.ApplicationTestConfig;
import com.task.dto.HistoryRequestDTO;
import com.task.dto.HistoryResponseDTO;
import com.task.model.ChargeHistoryItem;
import com.task.repository.ChargeHistoryRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.task.TestUtils.buildHistoryItem;
import static com.task.TestUtils.readFileAsString;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
public class ChargeServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChargeService chargeService;

    @Before
    public void before() {
        //2019-07-06 11:32:10 UTC
        ChargeHistoryItem chargeHistoryItem7 = buildHistoryItem((float) 100, ZonedDateTime.of(2019, 7, 6, 23, 47, 10, 0, ZoneId.of("Etc/UTC")));
        //2020-09-11 21:32:10 UTC
        ChargeHistoryItem chargeHistoryItem6 = buildHistoryItem((float) 5, ZonedDateTime.of(2020, 9, 11, 21, 32, 10, 0, ZoneId.of("Etc/UTC")));
        //2020-10-27 19:17:10 UTC
        ChargeHistoryItem chargeHistoryItem5 = buildHistoryItem((float) 4.9, ZonedDateTime.of(2020, 10, 27, 19, 17, 10, 0, ZoneId.of("Etc/UTC")));
        //2020-10-29 04:04:00 UTC
        ChargeHistoryItem chargeHistoryItem1 = buildHistoryItem((float) 1.1, ZonedDateTime.of(2020, 10, 29, 11, 4, 0, 0, ZoneId.of("Asia/Jakarta")));
        //2020-10-29 08:56:10 UTC
        ChargeHistoryItem chargeHistoryItem2 = buildHistoryItem((float) 2.4, ZonedDateTime.of(2020, 10, 29, 11, 56, 10, 0, ZoneId.of("Europe/Moscow")));
        //2020-10-29 09:56:10 UTC
        ChargeHistoryItem chargeHistoryItem3 = buildHistoryItem((float) 1.0, ZonedDateTime.of(2020, 10, 29, 12, 56, 10, 0, ZoneId.of("Europe/Moscow")));
        //2020-10-31 11:32:10 UTC
        ChargeHistoryItem chargeHistoryItem4 = buildHistoryItem((float) 1.9, ZonedDateTime.of(2020, 10, 31, 11, 32, 10, 0, ZoneId.of("Etc/UTC")));

        entityManager.persist(chargeHistoryItem1);
        entityManager.persist(chargeHistoryItem2);
        entityManager.persist(chargeHistoryItem3);
        entityManager.persist(chargeHistoryItem4);
        entityManager.persist(chargeHistoryItem5);
        entityManager.persist(chargeHistoryItem6);
        entityManager.persist(chargeHistoryItem7);
        entityManager.flush();
    }

    @Test
    public void getBalanceHistoryTest() throws Exception{
        //2020-10-29 07:32:10 UTC
        ZonedDateTime startTime = ZonedDateTime.of(2020, 10, 29, 7, 32, 10, 0, ZoneId.of("Etc/UTC"));
        //2020-10-29 12:32:10 UTC
        ZonedDateTime endTime = ZonedDateTime.of(2020, 10, 29, 12, 32, 10, 0, ZoneId.of("Etc/UTC"));
        HistoryRequestDTO historyRequest = new HistoryRequestDTO(startTime, endTime);

        List<HistoryResponseDTO> balanceHistory = chargeService.getBalanceHistory(historyRequest);
        String assetResult = new Gson().toJson(balanceHistory);
        String expectedResult = readFileAsString("src/test/resources/balance/balance_response_1.json");

        assertEquals(6, balanceHistory.size());
        assertEquals(expectedResult, assetResult);
    }

    @Test
    public void getBalanceHistoryTestSameTimeInDifferentTimeZones() throws Exception {
        //2020-10-29 14:32:10 UTC
        ZonedDateTime startTime = ZonedDateTime.of(2020, 10, 29, 14, 32, 10, 0, ZoneId.of("Etc/UTC"));
        //2020-10-29 16:32:10 UTC
        ZonedDateTime endTime = ZonedDateTime.of(2020, 10, 29, 16, 32, 10, 0, ZoneId.of("Etc/UTC"));

        //2020-10-29 07:32:10 UTC
        ZonedDateTime startTimeTZ = ZonedDateTime.of(2020, 10, 29, 14, 32, 10, 0, ZoneId.of("Asia/Jakarta"));
        //2020-10-29 09:32:10 UTC
        ZonedDateTime endTimeTZ = ZonedDateTime.of(2020, 10, 29, 16, 32, 10, 0, ZoneId.of("Asia/Jakarta"));

        HistoryRequestDTO historyRequest = new HistoryRequestDTO(startTime, endTime);
        HistoryRequestDTO historyRequestTZ = new HistoryRequestDTO(startTimeTZ, endTimeTZ);

        List<HistoryResponseDTO> balanceHistory = chargeService.getBalanceHistory(historyRequest);
        List<HistoryResponseDTO> balanceHistoryTZ = chargeService.getBalanceHistory(historyRequestTZ);
        String expectedResult = readFileAsString("src/test/resources/balance/balance_response_2.json");
        String expectedResultTZ = readFileAsString("src/test/resources/balance/balance_response_2_tz.json");
        String assetResult = new Gson().toJson(balanceHistory);
        String assetResultTZ = new Gson().toJson(balanceHistoryTZ);

        assertEquals(3, balanceHistory.size());
        assertEquals(expectedResult, assetResult);
        assertEquals(3, balanceHistoryTZ.size());
        assertEquals(expectedResultTZ, assetResultTZ);
    }
}
