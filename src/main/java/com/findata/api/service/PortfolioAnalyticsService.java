package com.findata.api.service;

import com.findata.api.model.dto.PortfolioMetricsRequest;
import com.findata.api.model.dto.PortfolioPosition;
import com.findata.api.model.entity.PriceHistory;
import com.findata.api.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioAnalyticsService {

    private final PriceHistoryRepository priceHistoryRepository;

    private void validateWeights(List<PortfolioPosition> positions) {
        BigDecimal totalWeight = positions.stream()
                .map(PortfolioPosition::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWeight.compareTo(new BigDecimal("0.99")) < 0 ||
            totalWeight.compareTo(new BigDecimal("1.01")) > 0) {
            throw new IllegalArgumentException(
                    String.format("Portfolio weights must sum to 1.0 (got %.2f)", totalWeight));
        }
    }

    private Map<String, List<PriceHistory>> fetchPriceData(PortfolioMetricsRequest request) {
        Map<String, List<PriceHistory>> priceData = new HashMap<>();

        for (PortfolioPosition position : request.getPositions()) {
            List<PriceHistory> prices = priceHistoryRepository.findByTickerAndDateBetween(
                    position.getTicker(),
                    request.getStartDate(),
                    request.getEndDate());
            priceData.put(position.getTicker(), prices);
        }

        return priceData;
    }

    private BigDecimal calculateReturn(List<PriceHistory> prices) {
        if (prices.size() < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal startPrice = prices.get(prices.size() - 1).getClose();
        BigDecimal endPrice = prices.get(0).getClose();

        return endPrice.subtract(startPrice)
                .divide(startPrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    private BigDecimal calculateVolatility(List<PriceHistory> prices) {
        if (prices.size() < 2) {
            return BigDecimal.ZERO;
        }

        List<BigDecimal> dailyReturns = new ArrayList<>();
        for (int i = 0; i < prices.size() - 1; i++) {
            BigDecimal currentPrice = prices.get(i).getClose();
            BigDecimal previousPrice = prices.get(i + 1).getClose();

            BigDecimal dailyReturn = currentPrice.subtract(previousPrice)
                    .divide(previousPrice, 4, RoundingMode.HALF_UP);
            dailyReturns.add(dailyReturn);
        }

        BigDecimal mean = dailyReturns.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(dailyReturns.size()), 6, RoundingMode.HALF_UP);

        BigDecimal variance = dailyReturns.stream()
                .map(ret -> ret.subtract(mean).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(dailyReturns.size()), 6, RoundingMode.HALF_UP);

        double volatility = Math.sqrt(variance.doubleValue()) * Math.sqrt(252) * 100;

        return BigDecimal.valueOf(volatility).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePortfolioVolatility(
            List<PortfolioPosition> positions,
            Map<String, List<PriceHistory>> priceData) {
        BigDecimal weightedVolatility = BigDecimal.ZERO;

        for (PortfolioPosition position : positions) {
            List<PriceHistory> prices = priceData.get(position.getTicker());
            BigDecimal stockVolatility = calculateVolatility(prices);
            BigDecimal weightedContribution = stockVolatility.multiply(position.getWeight());
            weightedVolatility = weightedVolatility.add(weightedContribution);
        }

        return weightedVolatility;
    }
}
