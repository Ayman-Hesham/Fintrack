package com.fintrack.fintrack.service;

import com.fintrack.fintrack.dto.TransactionDTO.TransactionResponse;
import com.fintrack.fintrack.dto.bankAccountDTO.BankAccountResponse;
import com.fintrack.fintrack.dto.dashboardDTO.DashboardResponse;
import com.fintrack.fintrack.dto.dashboardDTO.ExpenseByCategoryDTO;
import com.fintrack.fintrack.dto.dashboardDTO.MonthlyDataDTO;
import com.fintrack.fintrack.mapper.BankAccountMapper;
import com.fintrack.fintrack.mapper.TransactionMapper;
import com.fintrack.fintrack.model.Transaction;
import com.fintrack.fintrack.model.TransactionType;
import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.repository.BankAccountRepository;
import com.fintrack.fintrack.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionMapper transactionMapper;
    private final BankAccountMapper bankAccountMapper;

    public DashboardService(TransactionRepository transactionRepository,
            BankAccountRepository bankAccountRepository,
            TransactionMapper transactionMapper,
            BankAccountMapper bankAccountMapper) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionMapper = transactionMapper;
        this.bankAccountMapper = bankAccountMapper;
    }

    public DashboardResponse getDashboardData(User user) {
        List<Transaction> allTransactions = transactionRepository.findByUserId(user.getId());

        BigDecimal totalIncome = allTransactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = allTransactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSavings = totalIncome.subtract(totalExpenses);

        List<BankAccountResponse> accounts = bankAccountRepository.findByUserId(user.getId())
                .stream()
                .map(bankAccountMapper::toBankAccountResponse)
                .collect(Collectors.toList());

        List<TransactionResponse> recentTransactions = allTransactions.stream()
                .sorted((t1, t2) -> t2.getDate().compareTo(t1.getDate()))
                .limit(10)
                .map(transactionMapper::toTransactionResponse)
                .collect(Collectors.toList());

        List<MonthlyDataDTO> monthlyData = calculateMonthlyData(allTransactions);
        List<ExpenseByCategoryDTO> expensesByCategory = calculateExpensesByCategory(allTransactions);

        return new DashboardResponse(
                totalIncome,
                totalExpenses,
                totalSavings,
                accounts,
                recentTransactions,
                monthlyData,
                expensesByCategory);
    }

    private List<MonthlyDataDTO> calculateMonthlyData(List<Transaction> transactions) {
        List<MonthlyDataDTO> monthlyData = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM");

        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            LocalDate monthStart = monthDate.withDayOfMonth(1);
            LocalDate monthEnd = monthDate.withDayOfMonth(monthDate.lengthOfMonth());

            String monthName = monthDate.format(monthFormatter);

            BigDecimal income = transactions.stream()
                    .filter(t -> t.getTransactionType() == TransactionType.INCOME)
                    .filter(t -> !t.getDate().isBefore(monthStart) && !t.getDate().isAfter(monthEnd))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal expenses = transactions.stream()
                    .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
                    .filter(t -> !t.getDate().isBefore(monthStart) && !t.getDate().isAfter(monthEnd))
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            monthlyData.add(new MonthlyDataDTO(monthName, income, expenses));
        }

        return monthlyData;
    }

    private List<ExpenseByCategoryDTO> calculateExpensesByCategory(List<Transaction> transactions) {
        Map<String, ExpenseByCategoryDTO> categoryExpenses = new HashMap<>();

        transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
                .forEach(t -> {
                    String categoryName = t.getCategory().getName();
                    String color = t.getCategory().getColor().getHexCode();

                    categoryExpenses.compute(categoryName, (key, existing) -> {
                        if (existing == null) {
                            return new ExpenseByCategoryDTO(categoryName, t.getAmount(), color);
                        } else {
                            existing.setAmount(existing.getAmount().add(t.getAmount()));
                            return existing;
                        }
                    });
                });

        return new ArrayList<>(categoryExpenses.values());
    }
}
