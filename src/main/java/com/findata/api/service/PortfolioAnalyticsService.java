package com.findata.api.service;

import com.findata.api.model.dto.PortfolioPosition;
import com.findata.api.repository.PriceHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Port;
import java.math.BigDecimal;
import java.util.List;

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
}
