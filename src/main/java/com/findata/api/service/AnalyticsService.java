package com.findata.api.service;

import com.findata.api.model.dto.StockAnalytics;
import com.findata.api.model.entity.PriceHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final PriceHistoryService priceHistoryService;

    public StockAnalytics calculateAnalytics(String ticker) {
        List<PriceHistory> prices = priceHistoryService.getPriceHistory(ticker);

        if (prices.isEmpty()) {
            log.warn("No price data found for ticker: {}", ticker);
            return null;
        }

        PriceHistory latest = prices.get(0);

        return StockAnalytics.builder()
                .ticker(ticker)
                .asOfDate(latest.getDate())
                .currentPrice(latest.getClose())
                .previousClose(getPreviousClose(prices))
                .dailyChange(calculateDailyChange(prices))
                .dailyChangePercent(calculateDailyChangePercent(prices))
                .weeklyChange(calculatePeriodChange(prices, 7))
                .weeklyChangePercent(calculatePeriodChangePercent(prices, 7))
                .monthlyChange(calculatePeriodChange(prices, 30))
                .monthlyChangePercent(calculatePeriodChangePercent(prices, 30))
                .movingAverage50Day(calculateMovingAverage(prices, 50))
                .movingAverage200Day(calculateMovingAverage(prices, 200))
                .volatility30Day(calculateVolatility(prices, 30))
                .week52High(calculate52WeekHigh(prices))
                .week52Low(calculate52WeekLow(prices))
                .averageVolume30Day(calculateAverageVolume(prices, 30))
                .build();
    }

    private BigDecimal getPreviousClose(List<PriceHistory> prices) {
        return prices.size() > 1 ? prices.get(1).getClose() : BigDecimal.ZERO;
    }

    private BigDecimal calculateDailyChange(List<PriceHistory> prices) {
        if (prices.size() < 2) return BigDecimal.ZERO;
        return prices.get(0).getClose().subtract(prices.get(1).getClose());
    }

    private BigDecimal calculateDailyChangePercent(List<PriceHistory> prices) {
        if (prices.size() < 2) return BigDecimal.ZERO;
        BigDecimal current = prices.get(0).getClose();
        BigDecimal previous = prices.get(1).getClose();
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calculatePeriodChange(List<PriceHistory> prices, int days) {
        if (prices.size() < days) return BigDecimal.ZERO;
        return prices.get(0).getClose().subtract(prices.get(days - 1).getClose());
    }

    private BigDecimal calculatePeriodChangePercent(List<PriceHistory> prices, int days) {
        if (prices.size() < days) return BigDecimal.ZERO;
        BigDecimal current = prices.get(0).getClose();
        BigDecimal past = prices.get(days - 1).getClose();
        return current.subtract(past)
                .divide(past, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal calculateMovingAverage(List<PriceHistory> prices, int days) {
        if (prices.size() < days) return BigDecimal.ZERO;

        BigDecimal sum = prices.stream()
                .limit(days)
                .map(PriceHistory::getClose)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(days), 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateVolatility(List<PriceHistory> prices, int days) {
        if (prices.size() < days) return BigDecimal.ZERO;

        List<BigDecimal> returns = prices.stream()
                .limit(days)
                .map(PriceHistory::getClose)
                .toList();

        BigDecimal mean = returns.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(returns.size()), 4, RoundingMode.HALF_UP);

        BigDecimal variance = returns.stream()
                .map(price -> price.subtract(mean).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(returns.size()), 4, RoundingMode.HALF_UP);

        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()))
                .setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculate52WeekHigh(List<PriceHistory> prices) {
        return prices.stream()
                .limit(252)
                .map(PriceHistory::getHigh)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculate52WeekLow(List<PriceHistory> prices) {
        return prices.stream()
                .limit(252)
                .map(PriceHistory::getLow)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private Long calculateAverageVolume(List<PriceHistory> prices, int days) {
        if (prices.size() < days) return 0L;

        return (long) prices.stream()
                .limit(days)
                .mapToLong(PriceHistory::getVolume)
                .average()
                .orElse(0.0);
    }
}
