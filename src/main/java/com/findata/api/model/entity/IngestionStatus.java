package com.findata.api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ingestion_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngestionStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_started_at", nullable = false)
    private LocalDateTime jobStartedAt;

    @Column(name = "job_completed_at")
    private LocalDateTime jobCompletedAt;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(name = "total_stocks")
    private Integer totalStocks;

    @Column(name = "stocks_succeeded")
    private Integer stocksSucceeded;

    @Column(name = "stocks_failed")
    private Integer stocksFailed;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (stocksSucceeded == null) {
            stocksSucceeded = 0;
        }
        if (stocksFailed == null) {
            stocksFailed = 0;
        }
    }

    public enum JobStatus {
        RUNNING,
        COMPLETED,
        FAILED
    }
}
