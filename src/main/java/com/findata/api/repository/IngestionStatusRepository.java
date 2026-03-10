package com.findata.api.repository;

import com.findata.api.model.entity.IngestionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngestionStatusRepository extends JpaRepository<IngestionStatus, Long> {

    Optional<IngestionStatus> findFirstByOrderByJobStartedAtDesc();

    List<IngestionStatus> findTop10ByOrderByJobStartedAtDesc();

    List<IngestionStatus> findByStatus(IngestionStatus.JobStatus status);
}
