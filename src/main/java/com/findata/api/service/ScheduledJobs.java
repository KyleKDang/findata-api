package com.findata.api.service;

import com.findata.api.model.entity.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledJobs {

    private final StockService stockService;
    private final AlphaVantageService alphaVantageService;

    @Scheduled(cron = "0 0 18 * * * ", zone = "America/New_York")
    public void updateAllStockPrices() {
        log.info("Starting scheduled stock price update...");

        List<Stock> stocks = stockService.getAllStocks();
        log.info("Found {} stocks to update...", stocks.size());

        int successCount = 0;
        int failureCount = 0;

        for (Stock stock : stocks) {
            try {
                alphaVantageService.fetchAndSaveDailyPrices(stock.getTicker());
                successCount++;

                Thread.sleep(13000);

            } catch (Exception e) {
                log.error("Failed to update prices for {}: {}", stock.getTicker(), e.getMessage());
                failureCount++;
            }
        }

        log.info("Scheduled update complete. {} succeeded, {} failed", successCount, failureCount);
    }
}
