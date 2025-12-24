package com.findata.api.model.entity;

import jakarta.persistence.*;
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
    @Column(length = 10, nullable = false)
    private String ticker;

    @Column(name = "company_name", length = 255, nullable = false)
    private String companyName;

    @Column(length = 100)
    private String sector;

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
