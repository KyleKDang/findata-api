package com.findata.api.controller;

import com.findata.api.model.dto.PortfolioMetrics;
import com.findata.api.model.dto.PortfolioMetricsRequest;
import com.findata.api.service.PortfolioAnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioAnalyticsService portfolioAnalyticsService;

    @PostMapping("/metrics")
    public ResponseEntity<PortfolioMetrics> calculatePortfolioMetrics(
            @Valid @RequestBody PortfolioMetricsRequest request) {

        PortfolioMetrics metrics = portfolioAnalyticsService.calculatePortfolioMetrics(request);
        return ResponseEntity.ok(metrics);
    }
}
