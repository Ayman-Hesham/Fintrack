package com.fintrack.fintrack.repository;

import com.fintrack.fintrack.model.Budget;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(Long userId);
    Boolean existsByUserIdAndCategoryId(Long userId, Long categoryId);
}
