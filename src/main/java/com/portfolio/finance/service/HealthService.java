package com.portfolio.finance.service;

import com.portfolio.finance.dto.HealthResponse;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public HealthResponse getHealth() {
        return HealthResponse.builder()
                .status("UP")
                .service("personal-finance-api")
                .build();
    }
}
