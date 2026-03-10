package com.findata.api.controller;

import com.findata.api.model.entity.IngestionStatus;
import com.findata.api.repository.IngestionStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingestion")
@RequiredArgsConstructor
public class IngestionStatusController {

    private final IngestionStatusRepository ingestionStatusRepository;

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
}
