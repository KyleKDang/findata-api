package com.findata.api.controller;

import com.findata.api.service.AlphaVantageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final AlphaVantageService alphaVantageService;

    @GetMapping("/fetch/{ticker}")
    public ResponseEntity<String> testFetch(@PathVariable String ticker) {
        String response = alphaVantageService.fetchDailyPrices(ticker);
        return ResponseEntity.ok(response);
    }
}
