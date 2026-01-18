package com.fintrack.fintrack.service;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import com.fintrack.fintrack.repository.TransactionRepository;
import com.fintrack.fintrack.mapper.TransactionMapper;
import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.model.Transaction;
import com.fintrack.fintrack.model.TransactionType;
import com.fintrack.fintrack.model.Category;
import com.fintrack.fintrack.model.BankAccount;

import jakarta.transaction.Transactional;
import lombok.Data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintrack.fintrack.dto.BudgetDTO.BudgetResponse;
import com.fintrack.fintrack.dto.TransactionDTO.*;
import com.fintrack.fintrack.specification.TransactionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
@Transactional
public class TransactionService {
    // Static ObjectMapper singleton - avoids creating new instance on each call
    private static final ObjectMapper MAPPER;
    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
    }

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final BankAccountService bankAccountService;
    private final TransactionMapper transactionMapper;
    private final BudgetService budgetService;
    private final GeminiService geminiService;

    public TransactionService(TransactionRepository transactionRepository,
            TransactionMapper transactionMapper,
            CategoryService categoryService,
            BankAccountService bankAccountService,
            @Lazy BudgetService budgetService,
            GeminiService geminiService) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.categoryService = categoryService;
        this.bankAccountService = bankAccountService;
        this.budgetService = budgetService;
        this.geminiService = geminiService;
    }

    public Page<TransactionResponse> getAllTransactions(User user, Pageable pageable) {
        Specification<Transaction> spec = (root, query, cb) -> cb.equal(root.get("bankAccount").get("user").get("id"),
                user.getId());
        return transactionRepository.findAll(spec, pageable).map(transactionMapper::toTransactionResponse);
    }

    public Page<TransactionResponse> searchTransactions(String query, User user, Pageable pageable) {
        Specification<Transaction> spec = TransactionSpecification.searchBy(query, user);
        return transactionRepository.findAll(spec, pageable).map(transactionMapper::toTransactionResponse);
    }

    public Page<TransactionResponse> filterTransactions(TransactionFilterDTO filter, User user, Pageable pageable) {
        Specification<Transaction> spec = TransactionSpecification.filterBy(filter, user);
        return transactionRepository.findAll(spec, pageable).map(transactionMapper::toTransactionResponse);
    }

    public TransactionResponse createTransaction(CreateTransactionRequest dto, User user) {
        Transaction transaction = transactionMapper.toEntity(dto);
        BankAccount bankAccount = bankAccountService.getBankAccountById(dto.getBankAccountId());

        if (transaction.getTransactionType() == TransactionType.INCOME) {
            bankAccount.setBalance(bankAccount.getBalance().add(transaction.getAmount()));
        } else {
            bankAccount.setBalance(bankAccount.getBalance().subtract(transaction.getAmount()));
        }

        transaction.setBankAccount(bankAccount);
        transaction.setManual(true);

        Category category = categoryService.getCategoryById(dto.getCategoryId());
        transaction.setCategory(category);

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            transaction.setDescription("Transaction");
        } else {
            transaction.setDescription(dto.getDescription());
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        bankAccountService.updateBankAccount(bankAccount);
        return transactionMapper.toTransactionResponse(savedTransaction);
    }

    public BigDecimal getTotalSpentInCategoryForUser(Long userId, Long categoryId) {
        BigDecimal totalSpent = transactionRepository.sumAmountByUserIdAndCategoryId(userId, categoryId);
        return totalSpent != null ? totalSpent : BigDecimal.ZERO;
    }

    public Map<Long, BigDecimal> getCategorySpendingForUser(Long userId) {
        List<Object[]> results = transactionRepository.findTotalSpentByUserIdGroupByCategory(userId);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO));
    }

    public List<TransactionResponse> syncTransactions(Long bankAccountId, User user) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(bankAccountId);

        if (!bankAccount.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Bank account does not belong to user");
        }

        LocalDateTime lastSync = bankAccount.getLastSync();
        LocalDateTime today = LocalDateTime.now();

        long daysSinceLastSync = ChronoUnit.DAYS.between(lastSync, today);

        if (daysSinceLastSync <= 0) {
            return Collections.emptyList();
        }

        List<BudgetResponse> budgets = budgetService.getBudgetsForUser(user);
        List<Category> categories = categoryService.getCategoriesForUser(user);

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Generate a JSON array of mock bank transactions for the last ")
                .append(daysSinceLastSync).append(" days (from ").append(lastSync).append(" to ").append(today)
                .append("). ")
                .append("The user has a bank account balance of ").append(bankAccount.getBalance()).append(". ");

        promptBuilder.append("The user has the following categories available:\n");
        for (Category category : categories) {
            promptBuilder.append(category.getId()).append(": ").append(category.getName()).append(", ");
        }
        promptBuilder.append("\n");

        if (budgets.isEmpty()) {
            promptBuilder.append(
                    "The user has no specific budgets set. Generate realistic transactions based on the available categories and balance. ");
        } else {
            promptBuilder
                    .append("The user has the following budgets per category along with how much they have spent: ");
            for (BudgetResponse budget : budgets) {
                promptBuilder.append(budget.getCategoryName()).append(": ").append(budget.getAmount())
                        .append(": spent-")
                        .append(budget.getSpentAmount()).append(", ");
            }
            promptBuilder.append(
                    ". Generate realistic transactions (expenses and potentially income) based on these budgets and the balance. ");
        }

        promptBuilder.append("If the date range includes the start of a month, include a salary income transaction. ")
                .append("Each transaction object should have: 'amount' (number), 'date' (YYYY-MM-DD), 'description' (string), ")
                .append("'type' ('INCOME' or 'EXPENSE'), and 'categoryId' (integer, referring to the ID of the category from the provided list). ")
                .append("Output ONLY the JSON array.");

        try {
            String jsonResponse = geminiService.generateTransactions(promptBuilder.toString());
            jsonResponse = jsonResponse
                    .replaceAll("^```(json)?\\s*", "")
                    .replaceAll("\\s*```$", "")
                    .strip();

            List<MockTransactionDTO> mockTransactions = MAPPER.readValue(jsonResponse,
                    new TypeReference<List<MockTransactionDTO>>() {
                    });

            Map<Long, Category> categoryMap = categories.stream()
                    .collect(Collectors.toMap(Category::getId, Function.identity()));

            Category fallbackCategory = categories.stream()
                    .filter(c -> c.getName().equals("Other"))
                    .findFirst()
                    .orElse(categories.get(0));

            BigDecimal totalExpense = BigDecimal.ZERO;
            BigDecimal totalIncome = BigDecimal.ZERO;

            List<Transaction> transactionsToSave = new ArrayList<>(mockTransactions.size());

            for (MockTransactionDTO mock : mockTransactions) {
                Transaction t = new Transaction(
                        mock.getAmount(),
                        mock.getDate(),
                        mock.getDescription(),
                        TransactionType.valueOf(mock.getType()),
                        false,
                        bankAccount);

                Category category = (mock.getCategoryId() != null)
                        ? categoryMap.get(mock.getCategoryId())
                        : fallbackCategory;

                t.setCategory(category);
                transactionsToSave.add(t);

                if (t.getTransactionType() == TransactionType.INCOME) {
                    totalIncome = totalIncome.add(t.getAmount());
                } else {
                    totalExpense = totalExpense.add(t.getAmount());
                }
            }

            List<Transaction> savedTransactions = transactionRepository.saveAll(transactionsToSave);

            bankAccount.setBalance(bankAccount.getBalance().add(totalIncome).subtract(totalExpense));
            bankAccount.setLastSync(LocalDateTime.now());
            bankAccountService.updateBankAccount(bankAccount);

            return savedTransactions.stream().map(transactionMapper::toTransactionResponse)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("Failed to sync transactions: " + e.getMessage(), e);
        }
    }

    @Data
    static class MockTransactionDTO {
        private BigDecimal amount;
        private LocalDate date;
        private String description;
        private String type;
        private Long categoryId;
    }
}
