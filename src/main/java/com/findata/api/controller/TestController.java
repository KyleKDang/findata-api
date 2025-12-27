package com.findata.api.controller;

import com.findata.api.model.entity.PriceHistory;
import com.findata.api.service.AlphaVantageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final AlphaVantageService alphaVantageService;

    @GetMapping("/fetch-and-save/{ticker}")
    public ResponseEntity<String> testFetchAndSave(@PathVariable String ticker) {
        List<PriceHistory> savedPrices = alphaVantageService.fetchAndSaveDailyPrices(ticker);
        return ResponseEntity.ok("Successfully saved " + savedPrices.size() + " price records for " + ticker);
    }
}
