package com.fintrack.fintrack.dto.BudgetDTO;

import com.fintrack.fintrack.model.BudgetPeriod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {
    private Long id;
    private String categoryName;
    private String categoryIcon;
    private BigDecimal amount;
    private BigDecimal spentAmount;
    private BudgetPeriod period;
}
