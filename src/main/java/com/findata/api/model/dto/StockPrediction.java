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
public class StockPrediction {

    private String ticker;
    private String modelType;
    private LocalDate predictionDate;
    private List<DailyPrediction> predictions;
    private ModelMetrics metrics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyPrediction {
        private LocalDate date;
        private BigDecimal predictedPrice;
        private BigDecimal confidenceLower;
        private BigDecimal confidenceUpper;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModelMetrics {
        private BigDecimal rmse;
        private BigDecimal mae;
        private BigDecimal rSquared;
        private int trainSize;
        private int testSize;
    }
}
