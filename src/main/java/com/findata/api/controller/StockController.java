package com.findata.api.controller;

import com.findata.api.model.entity.Stock;
import com.findata.api.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<Stock> createStock(@Valid @RequestBody Stock stock) {
        Stock savedStock = stockService.saveStock(stock);
        URI location = URI.create("/api/stocks/" + savedStock.getTicker());
        return ResponseEntity.created(location).body(savedStock);
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<Stock> getStock(@PathVariable String ticker) {
        return stockService.getStockByTicker(ticker)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    @DeleteMapping("/{ticker}")
    public ResponseEntity<Void> deleteStock(@PathVariable String ticker) {
        stockService.deleteStock(ticker);
        return ResponseEntity.noContent().build();
    }
}
