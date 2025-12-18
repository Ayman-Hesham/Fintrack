package com.fintrack.fintrack.service;

import com.fintrack.fintrack.model.JobRequest;
import com.fintrack.fintrack.repository.JobRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final AsyncJobProcessor asyncJobProcessor;

    public JobService(JobRepository jobRepository, AsyncJobProcessor asyncJobProcessor) {
        this.jobRepository = jobRepository;
        this.asyncJobProcessor = asyncJobProcessor;
    }

    public UUID initiateSyncJob(String idempotencyKey, Long bankAccountId, Long userId) {
        // 1. Check if job exists
        Optional<JobRequest> existingJob = jobRepository.findByIdempotencyKey(idempotencyKey);
        if (existingJob.isPresent()) {
            return existingJob.get().getJobId();
        }

        // 2. Try to create new job
        try {
            JobRequest newJob = new JobRequest(idempotencyKey, bankAccountId, userId);
            newJob = jobRepository.save(newJob);

            // 3. Trigger Async Processing
            asyncJobProcessor.processSyncJob(newJob.getJobId());

            return newJob.getJobId();
        } catch (DataIntegrityViolationException e) {
            // Race condition: another thread inserted the key just now
            return jobRepository.findByIdempotencyKey(idempotencyKey)
                    .map(JobRequest::getJobId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Job should exist but not found after constraint violation"));
        }
    }

    public JobRequest getJob(UUID jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with ID: " + jobId));
    }
}
