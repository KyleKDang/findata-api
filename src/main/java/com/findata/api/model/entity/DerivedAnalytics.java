package com.findata.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "derived_analytics",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_analytics_ticker_date", columnNames = {"ticker", "as_of_date"})
        },
        indexes = {
                @Index(name = "idx_analytics_ticker", columnList = "ticker"),
                @Index(name = "idx_analytics_date", columnList = "as_of_date"),
                @Index(name = "idx_analytics_ticker_date", columnList = "ticker,as_of_date")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DerivedAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String ticker;

    @Column(name = "as_of_date", nullable = false)
    private LocalDate asOfDate;

    @Column(name = "current_price", precision = 12, scale = 4, nullable = false)
    private BigDecimal currentPrice;

    @Column(name = "previous_close", precision = 12, scale = 4)
    private BigDecimal previousClose;

    @Column(name = "daily_change", precision = 12, scale = 4)
    private BigDecimal dailyChange;

    @Column(name = "daily_change_percent", precision = 8, scale = 4)
    private BigDecimal dailyChangePercent;

    @Column(name = "weekly_change", precision = 12, scale = 4)
    private BigDecimal weeklyChange;

    @Column(name = "weekly_change_percent", precision = 8, scale = 4)
    private BigDecimal weeklyChangePercent;

    @Column(name = "monthly_change", precision = 12, scale = 4)
    private BigDecimal monthlyChange;

    @Column(name = "monthly_change_percent", precision = 8, scale = 4)
    private BigDecimal monthlyChangePercent;

    @Column(name = "moving_average_50_day", precision = 12, scale = 4)
    private BigDecimal movingAverage50Day;

    @Column(name = "moving_average_200_day", precision = 12, scale = 4)
    private BigDecimal movingAverage200Day;

    @Column(name = "volatility_30_day", precision = 8, scale = 4)
    private BigDecimal volatility30Day;

    @Column(name = "sharpe_ratio", precision = 8, scale = 4)
    private BigDecimal sharpeRatio;

    @Column(name = "week_52_high", precision = 12, scale = 4)
    private BigDecimal week52High;

    @Column(name = "week_52_low", precision = 12, scale = 4)
    private BigDecimal week52Low;

    @Column(name = "average_volume_30_day")
    private Long averageVolume30Day;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @PrePersist
    protected void onCreate() {
        calculatedAt = LocalDateTime.now();
    }
}
