package com.fintrack.fintrack.repository;

import com.fintrack.fintrack.model.Budget;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.EntityGraph;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    @EntityGraph(attributePaths = { "category" })
    List<Budget> findByUserId(Long userId);

    Boolean existsByUserIdAndCategoryId(Long userId, Long categoryId);
}
