package com.findata.api.controller;

import com.findata.api.repository.IngestionStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingestion")
@RequiredArgsConstructor
public class IngestionStatusController {

    private final IngestionStatusRepository ingestionStatusRepository;

}
