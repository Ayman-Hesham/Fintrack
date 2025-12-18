package com.fintrack.fintrack.dto.BudgetDTO;

import com.fintrack.fintrack.model.BudgetPeriod;
import com.fintrack.fintrack.model.Category;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBudgetRequest {
    @NotNull(message = "Budget category is required")
    private Category category;

    @NotNull(message = "Budget amount is required")
    private BigDecimal amount;

    @NotNull(message = "BUdget period is required")
    private BudgetPeriod period;
}
