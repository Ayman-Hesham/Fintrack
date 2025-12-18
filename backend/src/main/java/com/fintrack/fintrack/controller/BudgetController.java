package com.fintrack.fintrack.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.dto.BudgetDTO.*;
import com.fintrack.fintrack.service.BudgetService;
import jakarta.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping("/")
    public ResponseEntity<List<BudgetResponse>> getUserBudgets(@AuthenticationPrincipal User user) {
        List<BudgetResponse> res = budgetService.getBudgetsForUser(user);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/")
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody CreateBudgetRequest dto, @AuthenticationPrincipal User user) {
        BudgetResponse res = budgetService.createBudget(dto, user);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(@Valid @RequestBody CreateBudgetRequest dto, @PathVariable Long budgetId, @AuthenticationPrincipal User user) {
        BudgetResponse res = budgetService.updateBudget(budgetId, dto, user);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }
}
