package com.fintrack.fintrack.controller;

import com.fintrack.fintrack.model.JobRequest;
import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<?> initiateJob(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody Map<String, Long> payload,
            @AuthenticationPrincipal User user) {

        Long bankAccountId = payload.get("bankAccountId");

        if (bankAccountId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "bankAccountId is required"));
        }

        UUID jobId = jobService.initiateSyncJob(idempotencyKey, bankAccountId, user.getId());

        return ResponseEntity.accepted().body(Map.of(
                "jobId", jobId,
                "status", "SUBMITTED"));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobRequest> getJob(@PathVariable UUID jobId) {
        return ResponseEntity.ok(jobService.getJob(jobId));
    }
}
