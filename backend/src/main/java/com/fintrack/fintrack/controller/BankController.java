package com.fintrack.fintrack.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.service.BankAccountService;
import com.fintrack.fintrack.service.TransactionService;
import com.fintrack.fintrack.dto.bankAccountDTO.ConnectBankRequest;
import com.fintrack.fintrack.dto.TransactionDTO.TransactionResponse;
import com.fintrack.fintrack.dto.bankAccountDTO.BankAccountResponse;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/banks")
public class BankController {
    private final BankAccountService bankService;
    private final TransactionService transactionService;

    public BankController(BankAccountService bankService,
            TransactionService transactionService) {
        this.bankService = bankService;
        this.transactionService = transactionService;
    }

    @GetMapping("/")
    public ResponseEntity<List<BankAccountResponse>> getUserBankAccounts(@AuthenticationPrincipal User user) {
        List<BankAccountResponse> res = bankService.getAllBankAccountsByUserId(user.getId());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/")
    public ResponseEntity<BankAccountResponse> connectBankAccount(@Valid @RequestBody ConnectBankRequest dto,
            @AuthenticationPrincipal User user) {
        BankAccountResponse res = bankService.linkBankAccount(dto, user);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/{id}/sync")
    public ResponseEntity<List<TransactionResponse>> syncTransactions(@PathVariable Long id,
            @AuthenticationPrincipal User user) {
        List<TransactionResponse> res = transactionService.syncTransactions(id, user);
        return ResponseEntity.ok(res);
    }
}
