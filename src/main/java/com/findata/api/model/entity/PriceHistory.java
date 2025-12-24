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
    name = "price_history",
    indexes = {
        @Index(name = "idx_price_ticker_date", columnList = "ticker,date"),
        @Index(name = "idx_price_date", columnList = "date")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_ticker_date", columnNames = {"ticker", "date"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String ticker;

    @Column(nullable = false)
    private LocalDate date;

    @Column(precision = 12, scale = 4, nullable = false)
    private BigDecimal open;

    @Column(precision = 12, scale = 4, nullable = false)
    private BigDecimal high;

    @Column(precision = 12, scale = 4, nullable = false)
    private BigDecimal low;

    @Column(precision = 12, scale = 4, nullable = false)
    private BigDecimal close;

    @Column(nullable = false)
    private Long volume;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
