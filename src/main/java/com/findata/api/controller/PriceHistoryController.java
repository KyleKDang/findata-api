package com.findata.api.controller;

import com.findata.api.model.entity.PriceHistory;
import com.findata.api.service.PriceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    @PostMapping
    public ResponseEntity<PriceHistory> createPrice(@RequestBody PriceHistory priceHistory) {
        PriceHistory savedPrice = priceHistoryService.savePrice(priceHistory);
        URI location = URI.create("/api/prices/" + savedPrice.getId());
        return ResponseEntity.created(location).body(savedPrice);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<PriceHistory>> createPrices(@RequestBody List<PriceHistory> prices) {
        List<PriceHistory> savedPrices = priceHistoryService.savePrices(prices);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPrices);
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<List<PriceHistory>> getPriceHistory(@PathVariable String ticker) {
        List<PriceHistory> prices = priceHistoryService.getPriceHistory(ticker);
        return ResponseEntity.ok(prices);
    }

    @GetMapping("/{ticker}/latest")
    public ResponseEntity<PriceHistory> getLatestPrice(@PathVariable String ticker) {
        return priceHistoryService.getLatestPrice(ticker)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{ticker}/range")
    public ResponseEntity<List<PriceHistory>> getPriceRange(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PriceHistory> prices = priceHistoryService.getPriceHistoryInRange(ticker, startDate, endDate);
        return ResponseEntity.ok(prices);
    }
}
