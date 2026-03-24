package com.findata.api.controller;

import com.findata.api.model.entity.IngestionStatus;
import com.findata.api.repository.IngestionStatusRepository;
import com.findata.api.service.ScheduledJobs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ingestion")
@RequiredArgsConstructor
@Slf4j
public class IngestionStatusController {

    private final IngestionStatusRepository ingestionStatusRepository;
    private final ScheduledJobs scheduledJobs;

    @GetMapping("/status/latest")
    public ResponseEntity<IngestionStatus> getLatestJobStatus() {
        return ingestionStatusRepository.findFirstByOrderByJobStartedAtDesc()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/history")
    public ResponseEntity<List<IngestionStatus>> getJobHistory() {
        List<IngestionStatus> history = ingestionStatusRepository.findTop10ByOrderByJobStartedAtDesc();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/status/failed")
    public ResponseEntity<List<IngestionStatus>> getFailedJobs() {
        List<IngestionStatus> failed = ingestionStatusRepository.findByStatus(IngestionStatus.JobStatus.FAILED);
        return ResponseEntity.ok(failed);
    }

    @PostMapping("/trigger")
    public ResponseEntity<Map<String, String>> triggerIngestion() {
        log.info("Manual ingestion trigger requested");

        scheduledJobs.updateAllStockPrices();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Ingestion job triggered successfully");
        response.put("note", "This will take ~1 minute due to API rate limits (5 calls/min)");

        return ResponseEntity.ok(response);
    }
}
