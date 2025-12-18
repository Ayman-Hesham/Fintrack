package com.fintrack.fintrack.specification;

import com.fintrack.fintrack.dto.TransactionDTO.TransactionFilterDTO;
import com.fintrack.fintrack.model.Transaction;
import com.fintrack.fintrack.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> filterBy(TransactionFilterDTO filter, User user) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // User filter (always apply)
            predicates.add(criteriaBuilder.equal(root.get("bankAccount").get("user").get("id"), user.getId()));

            if (filter.getTransactionType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("transactionType"), filter.getTransactionType()));
            }

            if (filter.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), filter.getCategoryId()));
            }

            if (filter.getBankAccountId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("bankAccount").get("id"), filter.getBankAccountId()));
            }

            if (filter.getFromDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), filter.getFromDate()));
            }

            if (filter.getToDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), filter.getToDate()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Transaction> searchBy(String search, User user) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // User filter (always apply)
            predicates.add(criteriaBuilder.equal(root.get("bankAccount").get("user").get("id"), user.getId()));

            if (search != null && !search.isBlank()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
