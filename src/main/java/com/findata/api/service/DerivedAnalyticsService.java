package com.findata.api.service;

import com.findata.api.model.dto.StockAnalytics;
import com.findata.api.model.entity.DerivedAnalytics;
import com.findata.api.repository.DerivedAnalyticsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DerivedAnalyticsService {

    private final DerivedAnalyticsRepository derivedAnalyticsRepository;

    @Transactional
    public DerivedAnalytics saveDerivedAnalytics(StockAnalytics analytics) {
        DerivedAnalytics derived = DerivedAnalytics.builder()
                .ticker(analytics.getTicker())
                .asOfDate(analytics.getAsOfDate())
                .currentPrice(analytics.getCurrentPrice())
                .previousClose(analytics.getPreviousClose())
                .dailyChange(analytics.getDailyChange())
                .dailyChangePercent(analytics.getDailyChangePercent())
                .weeklyChange(analytics.getWeeklyChange())
                .weeklyChangePercent(analytics.getWeeklyChangePercent())
                .monthlyChange(analytics.getMonthlyChange())
                .monthlyChangePercent(analytics.getMonthlyChangePercent())
                .movingAverage50Day(analytics.getMovingAverage50Day())
                .movingAverage200Day(analytics.getMovingAverage200Day())
                .volatility30Day(analytics.getVolatility30Day())
                .sharpeRatio(analytics.getSharpeRatio())
                .week52High(analytics.getWeek52High())
                .week52Low(analytics.getWeek52Low())
                .averageVolume30Day(analytics.getAverageVolume30Day())
                .build();

        DerivedAnalytics saved = derivedAnalyticsRepository.save(derived);
        log.debug("Saved derived analytics for ticker: {} as of {}",
                analytics.getTicker(), analytics.getAsOfDate());

        return saved;
    }

    public Optional<DerivedAnalytics> getLatestAnalytics(String ticker) {
        return derivedAnalyticsRepository.findFirstByTickerOrderByAsOfDateDesc(ticker);
    }

    public Optional<DerivedAnalytics> getAnalyticsForDate(String ticker, LocalDate date) {
        return derivedAnalyticsRepository.findByTickerAndAsOfDate(ticker, date);
    }
}
