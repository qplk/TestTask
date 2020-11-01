package com.task.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.ApplicationTestConfig;
import com.task.TestUtils;
import com.task.dto.ChargeRequestDTO;
import com.task.dto.ChargeResponseDTO;
import com.task.dto.HistoryRequestDTO;
import com.task.dto.HistoryResponseDTO;
import com.task.repository.ChargeHistoryRepository;
import com.task.service.ChargeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.task.TestUtils.readFileAsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(BalanceController.class)
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ApplicationTestConfig.class)
public class BalanceControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChargeHistoryRepository chargeHistoryRepository;

    @MockBean
    private ChargeService chargeService;

    @Before
    public void before() throws Exception{
        ChargeResponseDTO successChargeResponse = new ChargeResponseDTO(1L, ZonedDateTime.of(2020, 10, 6, 7, 1, 1, 0, ZoneId.of("Europe/Moscow")));
        when(chargeService.processCharge(any(ChargeRequestDTO.class)))
                .thenReturn(successChargeResponse);
        List<HistoryResponseDTO> historyResponse = mapper.readValue(readFileAsString("src/test/resources/balance/balance_response_1.json"), new TypeReference<List<HistoryResponseDTO>>(){});
        when(chargeService.getBalanceHistory(any(HistoryRequestDTO.class)))
                .thenReturn(historyResponse);
    }

    @Test
    public void validChargeRequestTest() throws Exception {
        mockMvc.perform(post("/api/v1/charge").contentType(MediaType.APPLICATION_JSON)
                .content(readFileAsString("src/test/resources/charge/charge_request.json")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(readFileAsString("src/test/resources/charge/success_charge_response.json")));
    }

    @Test
    public void chargeWithInvalidAmountTest() throws Exception {
        mockMvc.perform(post("/api/v1/charge").contentType(MediaType.APPLICATION_JSON)
                .content(readFileAsString("{ \"datetime\": \"2020-10-06T07:01:01+01:00\", \"amount\": 0}")))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(readFileAsString("src\\test\\resources\\charge\\error_charge_response_invalid_amount.json")));
    }

    @Test
    public void chargeWithDateInFutureTest() throws Exception {
        mockMvc.perform(post("/api/v1/charge").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"datetime\": \"2100-10-06T07:01:01+01:00\", \"amount\": 1}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(readFileAsString("src/test/resources/charge/error_charge_response_future_date.json")));
    }

    @Test
    public void chargeWithInvalidDateTest() throws Exception {
        mockMvc.perform(post("/api/v1/charge").contentType(MediaType.APPLICATION_JSON)
                .content("{ \"datetime\": \"FakeDate\", \"amount\": 13}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void chargeRequestWithoutDateTest() throws Exception {
        mockMvc.perform(post("/api/v1/charge").contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 13}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getBalanceWhereStartTimeAfterEndTimeTest() throws Exception {
        mockMvc.perform(get("/api/v1/history").contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "     \"startDatetime\": \"2020-10-08T06:59:01+00:00\",\n" +
                        "     \"endDatetime\": \"2020-10-06T18:48:02+00:00\"\n" +
                        "   }"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(readFileAsString("src/test/resources/balance/error_balance_response_start_after_end.json")));
    }

    @Test
    public void getBalanceInFutureTest() throws Exception {
        mockMvc.perform(get("/api/v1/history").contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "     \"startDatetime\": \"2021-10-08T06:59:01+00:00\",\n" +
                        "     \"endDatetime\": \"2021-10-09T18:48:02+00:00\"\n" +
                        "   }"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json(readFileAsString("src/test/resources/balance/error_balance_response_in_future.json")));
    }
}
