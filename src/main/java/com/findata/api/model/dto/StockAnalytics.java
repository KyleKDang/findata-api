package com.findata.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAnalytics {

    private String ticker;
    private LocalDate asOfDate;

    private BigDecimal currentPrice;
    private BigDecimal previousClose;

    private BigDecimal dailyChange;
    private BigDecimal dailyChangePercent;

    private BigDecimal weeklyChange;
    private BigDecimal weeklyChangePercent;

    private BigDecimal monthlyChange;
    private BigDecimal monthlyChangePercent;

    private BigDecimal movingAverage50Day;
    private BigDecimal movingAverage200Day;

    private BigDecimal volatility30Day;

    private BigDecimal week52High;
    private BigDecimal week52Low;

    private Long averageVolume30Day;
}
