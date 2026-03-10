-- Migration: Create ingestion_status table for job tracking
-- Purpose: Track scheduled job runs for monitoring and debugging

CREATE TABLE ingestion_status (
    id BIGSERIAL PRIMARY KEY,
    job_started_at TIMESTAMP NOT NULL,
    job_completed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    total_stocks INTEGER,
    stocks_succeeded INTEGER DEFAULT 0,
    stocks_failed INTEGER DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ingestion_job_started ON ingestion_status(job_started_at DESC);
CREATE INDEX idx_ingestion_status ON ingestion_status(status);

COMMENT ON TABLE ingestion_status IS 'Tracks scheduled data ingestion job runs for monitoring and debugging';
COMMENT ON COLUMN ingestion_status.status IS 'Job status: RUNNING, COMPLETED, FAILED';
COMMENT ON COLUMN ingestion_status.job_started_at IS 'When the job started executing';
COMMENT ON COLUMN ingestion_status.job_completed_at IS 'When the job finished (success or failure)';