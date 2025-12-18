package com.fintrack.fintrack.dto.TransactionDTO;

import com.fintrack.fintrack.model.TransactionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {
    @NotNull(message = "Transaction date is required")
    private LocalDate date;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String description;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Bank account is required")
    private Long bankAccountId;
}
