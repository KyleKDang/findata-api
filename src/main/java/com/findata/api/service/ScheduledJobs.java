package com.findata.api.service;

import com.findata.api.model.dto.StockAnalytics;
import com.findata.api.model.entity.IngestionStatus;
import com.findata.api.model.entity.Stock;
import com.findata.api.repository.IngestionStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledJobs {

    private final StockService stockService;
    private final AlphaVantageService alphaVantageService;
    private final AnalyticsService analyticsService;
    private final DerivedAnalyticsService derivedAnalyticsService;
    private final IngestionStatusRepository ingestionStatusRepository;

    @Scheduled(cron = "0 0 18 * * * ", zone = "America/New_York")
    public void updateAllStockPrices() {
        log.info("Starting scheduled stock price update...");

        IngestionStatus jobStatus = IngestionStatus.builder()
                .jobStartedAt(LocalDateTime.now())
                .status(IngestionStatus.JobStatus.RUNNING)
                .build();
        jobStatus = ingestionStatusRepository.save(jobStatus);

        List<Stock> stocks = stockService.getAllStocks();
        log.info("Found {} stocks to update...", stocks.size());

        jobStatus.setTotalStocks(stocks.size());
        ingestionStatusRepository.save(jobStatus);

        int priceUpdateSuccess = 0;
        int priceUpdateFailure = 0;
        int analyticsSuccess = 0;
        int analyticsFailure = 0;

        try {
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

            jobStatus.setStatus(IngestionStatus.JobStatus.COMPLETED);
            jobStatus.setStocksSucceeded(priceUpdateSuccess);
            jobStatus.setStocksFailed(priceUpdateFailure);
            jobStatus.setJobCompletedAt(LocalDateTime.now());
            ingestionStatusRepository.save(jobStatus);

            log.info("Scheduled update complete. Prices: {} succeeded, {} failed. Analytics: {} succeeded, {} failed",
                    priceUpdateSuccess, priceUpdateFailure, analyticsSuccess, analyticsFailure);

        } catch (Exception e) {

            log.error("Scheduled job failed with error: {}", e.getMessage(), e);

            jobStatus.setStatus(IngestionStatus.JobStatus.FAILED);
            jobStatus.setStocksSucceeded(priceUpdateFailure);
            jobStatus.setStocksFailed(priceUpdateFailure);
            jobStatus.setErrorMessage(e.getMessage());
            jobStatus.setJobCompletedAt(LocalDateTime.now());
            ingestionStatusRepository.save(jobStatus);

            throw e;
        }
    }
}
