package com.findata.api.service;

import com.findata.api.model.dto.StockAnalytics;
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
    private final AnalyticsService analyticsService;
    private final DerivedAnalyticsService derivedAnalyticsService;

    @Scheduled(cron = "0 0 18 * * * ", zone = "America/New_York")
    public void updateAllStockPrices() {
        log.info("Starting scheduled stock price update...");

        List<Stock> stocks = stockService.getAllStocks();
        log.info("Found {} stocks to update...", stocks.size());

        int priceUpdateSuccess = 0;
        int priceUpdateFailure = 0;
        int analyticsSuccess = 0;
        int analyticsFailure = 0;

        for (Stock stock : stocks) {
            try {
                alphaVantageService.fetchAndSaveDailyPrices(stock.getTicker());
                priceUpdateSuccess++;

                try {
                    StockAnalytics analytics = analyticsService.calculateAnalytics(stock.getTicker());
                    if (analytics != null) {
                        derivedAnalyticsService.saveDerivedAnalytics(analytics);
                        analyticsSuccess++;
                        log.debug("Computed and saved analytics for {}", stock.getTicker());
                    }
                } catch (Exception e) {
                    log.warn("Failed to compute analytics for {}: {}", stock.getTicker(), e.getMessage());
                    analyticsFailure++;
                }

                Thread.sleep(13000);

            } catch (Exception e) {
                log.error("Failed to update prices for {}: {}", stock.getTicker(), e.getMessage());
                priceUpdateFailure++;
            }
        }

        log.info("Scheduled update complete. Prices: {} succeeded, {} failed. Analytics: {} succeeded, {} failed",
                priceUpdateSuccess, priceUpdateFailure, analyticsSuccess, analyticsFailure);
    }
}
