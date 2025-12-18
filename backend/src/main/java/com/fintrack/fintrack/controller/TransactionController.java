package com.fintrack.fintrack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import com.fintrack.fintrack.service.TransactionService;
import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.dto.TransactionDTO.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/")
    public ResponseEntity<Page<TransactionResponse>> getUserTransactions(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = { "date",
                    "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TransactionResponse> res = transactionService.getAllTransactions(user, pageable);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TransactionResponse>> searchTransactions(
            @RequestParam String query,
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = { "date",
                    "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TransactionResponse> res = transactionService.searchTransactions(query, user, pageable);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<TransactionResponse>> filterTransactions(
            @ModelAttribute TransactionFilterDTO filter,
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = { "date",
                    "id" }, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TransactionResponse> res = transactionService.filterTransactions(filter, user, pageable);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/")
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest dto,
            @AuthenticationPrincipal User user) {
        TransactionResponse res = transactionService.createTransaction(dto, user);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}
