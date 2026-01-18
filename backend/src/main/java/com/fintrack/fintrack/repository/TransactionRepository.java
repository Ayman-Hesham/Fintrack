package com.fintrack.fintrack.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.fintrack.fintrack.model.Transaction;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface TransactionRepository extends JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {

    @Override
    @EntityGraph(attributePaths = { "category", "bankAccount" })
    Page<Transaction> findAll(Specification<Transaction> spec, Pageable pageable);

    @Query("SELECT t.category.id, SUM(t.amount) FROM Transaction t WHERE t.bankAccount.user.id = :userId GROUP BY t.category.id")
    List<Object[]> findTotalSpentByUserIdGroupByCategory(Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.bankAccount.user.id = :userId")
    List<Transaction> findByUserId(Long userId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.bankAccount.user.id = :userId AND t.category.id = :categoryId")
    BigDecimal sumAmountByUserIdAndCategoryId(Long userId, Long categoryId);
}
