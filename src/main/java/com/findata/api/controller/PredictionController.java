package com.findata.api.controller;

import com.findata.api.model.dto.StockPrediction;
import com.findata.api.service.PredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Slf4j
public class PredictionController {

    private final PredictionService predictionService;

    @GetMapping("/{ticker}/predict")
    public ResponseEntity<StockPrediction> predictPrices(@PathVariable String ticker) {
        log.info("Received prediction request for ticker: {}", ticker);

        StockPrediction prediction = predictionService.predictPrices(ticker);
        return ResponseEntity.ok(prediction);
    }
}
