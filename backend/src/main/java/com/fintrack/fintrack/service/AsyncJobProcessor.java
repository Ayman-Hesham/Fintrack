package com.fintrack.fintrack.service;

import com.fintrack.fintrack.model.JobRequest;
import com.fintrack.fintrack.model.JobStatus;
import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.repository.JobRepository;
import com.fintrack.fintrack.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AsyncJobProcessor {

    private final JobRepository jobRepository;
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public AsyncJobProcessor(JobRepository jobRepository,
            TransactionService transactionService,
            UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @Async("taskExecutor")
    public void processSyncJob(UUID jobId) {
        JobRequest job = jobRepository.findById(jobId).orElse(null);
        if (job == null)
            return;

        try {
            job.setStatus(JobStatus.PROCESSING);

            jobRepository.save(job);
            User user = userRepository.findById(job.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            var syncedTransactions = transactionService.syncTransactions(job.getBankAccountId(), user);

            job.setStatus(JobStatus.COMPLETED);
            job.setResult("Successfully synced " + syncedTransactions.size() + " transactions.");
            jobRepository.save(job);

        } catch (Exception e) {
            job.setStatus(JobStatus.FAILED);
            job.setResult("Job failed: " + e.getMessage());
            jobRepository.save(job);
        }
    }
}
