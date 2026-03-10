package com.findata.api.controller;

import com.findata.api.model.entity.IngestionStatus;
import com.findata.api.repository.IngestionStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
