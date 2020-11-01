package com.task.service;

import com.task.dto.ChargeRequestDTO;
import com.task.dto.ChargeResponseDTO;
import com.task.model.ChargeHistoryItem;
import com.task.repository.ChargeHistoryRepository;
import com.task.service.impl.ChargeServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;


import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ChargeServiceImplTest {

    @InjectMocks
    private ChargeServiceImpl chargeService;

    @Mock
    private ChargeHistoryRepository chargeHistoryRepository;

    @Test
    public void saveChargeHistoryItemTest() {
        ChargeRequestDTO chargeRequest = new ChargeRequestDTO((float)2.1, ZonedDateTime.now());
        ChargeHistoryItem chargeHistoryItem = chargeRequest.toChargeHistoryItem();
        chargeHistoryItem.setId(1L);
        when(chargeHistoryRepository.save(chargeRequest.toChargeHistoryItem())).thenReturn(chargeHistoryItem);

        ChargeResponseDTO chargeResponse = chargeService.processCharge(chargeRequest);
        assertNotNull(chargeResponse);
        assertEquals(chargeRequest.getDatetime(), chargeResponse.getTransTime());
        assertNotEquals(ZonedDateTime.now(), chargeResponse.getTransTime());
    }
}
