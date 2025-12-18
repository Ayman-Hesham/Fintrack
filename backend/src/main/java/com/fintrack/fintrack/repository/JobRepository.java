package com.fintrack.fintrack.repository;

import com.fintrack.fintrack.model.JobRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<JobRequest, UUID> {
    Optional<JobRequest> findByIdempotencyKey(String idempotencyKey);
}
