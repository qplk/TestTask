package com.task;

import com.task.service.ChargeService;
import com.task.service.impl.ChargeServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ApplicationTestConfig {

    @Bean
    public ChargeService chargeService() {
        return new ChargeServiceImpl();
    }
}
