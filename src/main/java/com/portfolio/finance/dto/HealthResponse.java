package com.portfolio.finance.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class HealthResponse {
    String status;
    String service;
}
