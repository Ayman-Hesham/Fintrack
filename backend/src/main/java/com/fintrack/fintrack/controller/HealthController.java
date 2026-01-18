package com.fintrack.fintrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.sql.Connection;

@RestController
@RequestMapping("/")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/ready")
    public ResponseEntity<String> ready() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(1)) {
                return ResponseEntity.ok("OK");
            }
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Database connection invalid");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Database connection failed");
        }
    }

    @GetMapping("/alive")
    public ResponseEntity<String> alive() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] deadlockedThreads = threadBean.findDeadlockedThreads();

        if (deadlockedThreads != null && deadlockedThreads.length > 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deadlock detected");
        }
        return ResponseEntity.ok("OK");
    }
}
