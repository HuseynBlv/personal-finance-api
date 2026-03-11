package com.portfolio.finance.controller;

import com.portfolio.finance.dto.HealthResponse;
import com.portfolio.finance.service.HealthService;
import com.portfolio.finance.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.BASE_API)
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @GetMapping("/health")
    public HealthResponse health() {
        return healthService.getHealth();
    }
}
