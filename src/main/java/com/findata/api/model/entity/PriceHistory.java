package com.findata.api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Ticker cannot be blank")
    @Size(min = 1, max = 10, message = "Ticker must be between 1 and 10 characters")
    @Column(nullable = false, length = 10)
    private String ticker;

    @NotNull(message = "Date cannot be null")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "Open price cannot be null")
    @Positive(message = "Open price must be positive")
    @Column(precision = 12, scale = 4, nullable = false)
    private BigDecimal open;

    @NotNull(message = "High price cannot be null")
    @Positive(message = "High price must be positive")
    @Column(precision = 12, scale = 4, nullable = false)
    private BigDecimal high;

    @NotNull(message = "Low price cannot be null")
    @Positive(message = "Low price must be positive")
    @Column(precision = 12, scale = 4, nullable = false)
    private BigDecimal low;

    @NotNull(message = "Close price cannot be null")
    @Positive(message = "Close price must be positive")
    @Column(precision = 12, scale = 4, nullable = false)
    private BigDecimal close;

    @NotNull(message = "Volume cannot be null")
    @Positive(message = "Volume must be positive")
    @Column(nullable = false)
    private Long volume;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
