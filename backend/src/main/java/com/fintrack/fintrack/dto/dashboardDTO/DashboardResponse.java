package com.fintrack.fintrack.dto.dashboardDTO;

import com.fintrack.fintrack.dto.TransactionDTO.TransactionResponse;
import com.fintrack.fintrack.dto.bankAccountDTO.BankAccountResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal totalSavings;
    private List<BankAccountResponse> accounts;
    private List<TransactionResponse> recentTransactions;
    private List<MonthlyDataDTO> monthlyData;
    private List<ExpenseByCategoryDTO> expensesByCategory;
}
