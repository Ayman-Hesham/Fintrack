package com.fintrack.fintrack.service;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import com.fintrack.fintrack.repository.BudgetRepository;
import com.fintrack.fintrack.mapper.BudgetMapper;
import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.model.Budget;
import com.fintrack.fintrack.dto.BudgetDTO.*;
import com.fintrack.fintrack.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import java.util.List;
import java.math.BigDecimal;
import java.util.Map;

@Service
@Transactional
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final TransactionService transactionService;
    private final BudgetMapper budgetMapper;

    public BudgetService(BudgetRepository budgetRepository,
            BudgetMapper budgetMapper,
            @Lazy TransactionService transactionService) {
        this.budgetRepository = budgetRepository;
        this.budgetMapper = budgetMapper;
        this.transactionService = transactionService;
    }

    public List<BudgetResponse> getBudgetsForUser(User user) {
        List<Budget> budgets = budgetRepository.findByUserId(user.getId());

        if (budgets.isEmpty()) {
            return List.of();
        }

        Map<Long, BigDecimal> spendingByCategory = transactionService
                .getCategorySpendingForUser(user.getId());

        List<BudgetResponse> budgetResponses = budgets.stream().map(budget -> {
            BigDecimal spentAmount = spendingByCategory.getOrDefault(budget.getCategory().getId(), BigDecimal.ZERO);
            return budgetMapper.toBudgetResponse(budget, spentAmount, budget.getCategory());
        })
                .toList();

        return budgetResponses;
    }

    public BudgetResponse createBudget(CreateBudgetRequest dto, User user) {
        Budget budget = budgetMapper.toBudget(dto);
        budget.setUser(user);

        if (budgetRepository.existsByUserIdAndCategoryId(
                user.getId(), budget.getCategory().getId())) {
            throw new IllegalArgumentException("Budget already exists for this category");
        }

        Budget savedBudget = budgetRepository.save(budget);

        BigDecimal spentAmount = transactionService.getTotalSpentInCategoryForUser(
                user.getId(), savedBudget.getCategory().getId());

        return budgetMapper.toBudgetResponse(savedBudget, spentAmount, savedBudget.getCategory());
    }

    public BudgetResponse updateBudget(Long budgetId, CreateBudgetRequest dto, User user) {
        Budget existingBudget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));

        existingBudget.setCategory(dto.getCategory());
        existingBudget.setAmount(dto.getAmount());
        existingBudget.setPeriod(dto.getPeriod());
        Budget updatedBudget = budgetRepository.save(existingBudget);

        BigDecimal spentAmount = transactionService.getTotalSpentInCategoryForUser(
                user.getId(), updatedBudget.getCategory().getId());

        return budgetMapper.toBudgetResponse(updatedBudget, spentAmount, updatedBudget.getCategory());
    }

    public void deleteBudget(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));
        budgetRepository.delete(budget);
    }
}
