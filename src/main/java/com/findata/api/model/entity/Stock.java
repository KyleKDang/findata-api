package com.findata.api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "stocks",
    indexes = {
        @Index(name = "idx_stocks_sector", columnList = "sector"),
        @Index(name = "idx_stocks_market_cap", columnList = "market_cap")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @NotBlank(message = "Ticker cannot be blank")
    @Size(min = 1, max = 10, message = "Ticker must be between 1 and 10 characters")
    @Column(length = 10, nullable = false)
    private String ticker;

    @NotBlank(message = "Company name cannot be blank")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Column(name = "company_name", length = 255, nullable = false)
    private String companyName;

    @Size(max = 100, message = "Sector must not exceed 100 characters")
    @Column(length = 100)
    private String sector;

    @Positive(message = "Market cap must be positive")
    @Column(name = "market_cap")
    private Long marketCap;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
