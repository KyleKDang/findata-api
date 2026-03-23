package com.findata.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioMetrics {

    private List<PortfolioPosition> positions;
    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal portfolioReturnPercent;
    private BigDecimal portfolioVolatility;
    private BigDecimal sharpeRatio;

    private List<PositionMetrics> positionMetrics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PositionMetrics {
        private String ticker;
        private BigDecimal weight;
        private BigDecimal returnPercent;
        private BigDecimal volatility;
        private BigDecimal contribution;
    }
}
