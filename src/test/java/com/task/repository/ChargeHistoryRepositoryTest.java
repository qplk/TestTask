package com.task.repository;

import com.task.model.ChargeHistoryItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.task.TestUtils.buildHistoryItem;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ChargeHistoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChargeHistoryRepository chargeHistoryRepository;

    @Test
    public void saveChargeHistoryItemTest() {
        ChargeHistoryItem chi = new ChargeHistoryItem();
        chi.setAmount((float)3.1);
        chi.setDatetime(ZonedDateTime.now());

        entityManager.persist(chi);
        entityManager.flush();

        List<ChargeHistoryItem> all = chargeHistoryRepository.findAll();

        assertEquals(1, all.size());
        assertNotNull(chargeHistoryRepository.findById(1L));
    }

    @Test
    public void getQuantityByDateTest() {
        ZonedDateTime time = ZonedDateTime.now();
        for (int i = 0; i < 10; i++) {
            entityManager.persist(buildHistoryItem(i, time.minusHours(i)));
        }
        entityManager.flush();

        assertEquals(10, chargeHistoryRepository.findAll().size());
        assertEquals(45, chargeHistoryRepository.getQuantityForDate(time), 0);
        assertNull(chargeHistoryRepository.getQuantityForDate(time.minusDays(1)));
    }

    @Test
    public void checkDifferentTimeZonesTest() {
        //UTC 04:04:00
        ChargeHistoryItem chargeHistoryItem2 = buildHistoryItem((float) 1.1, ZonedDateTime.of(2020, 10, 29, 11, 4, 0, 0, ZoneId.of("Asia/Jakarta")));
        //UTC 08:56:10
        ChargeHistoryItem chargeHistoryItem1 = buildHistoryItem((float) 2.4, ZonedDateTime.of(2020, 10, 29, 11, 56, 10, 0, ZoneId.of("Europe/Moscow")));
        //UTC 09:56:10
        ChargeHistoryItem chargeHistoryItem4 = buildHistoryItem((float) 1.0, ZonedDateTime.of(2020, 10, 29, 12, 56, 10, 0, ZoneId.of("Europe/Moscow")));
        //UTC 11:32:10
        ChargeHistoryItem chargeHistoryItem3 = buildHistoryItem((float) 1.9, ZonedDateTime.of(2020, 10, 29, 11, 32, 10, 0, ZoneId.of("Etc/UTC")));

        entityManager.persist(chargeHistoryItem1);
        entityManager.persist(chargeHistoryItem2);
        entityManager.persist(chargeHistoryItem3);
        entityManager.persist(chargeHistoryItem4);
        entityManager.flush();

        assertEquals(4, chargeHistoryRepository.findAll().size());
        assertEquals((float)3.5, chargeHistoryRepository.getQuantityForDate(ZonedDateTime.of(2020, 10, 29, 8, 59, 10, 0, ZoneId.of("Etc/UTC"))), 0);
        assertEquals((float)1.1, chargeHistoryRepository.getQuantityForDate(ZonedDateTime.of(2020, 10, 29, 8, 54, 10, 0, ZoneId.of("Etc/UTC"))), 0);
        assertEquals((float)4.5, chargeHistoryRepository.getQuantityForDate(ZonedDateTime.of(2020, 10, 29, 13, 0, 10, 0, ZoneId.of("Europe/Moscow"))), 0);
        assertEquals((float)6.4, chargeHistoryRepository.getQuantityForDate(ZonedDateTime.of(2020, 10, 29, 18, 40, 10, 0, ZoneId.of("Asia/Jakarta"))), 0);
        assertEquals((float)4.5, chargeHistoryRepository.getQuantityForDate(ZonedDateTime.of(2020, 10, 29, 17, 40, 10, 0, ZoneId.of("Asia/Jakarta"))), 0);
    }
}
