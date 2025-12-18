package com.fintrack.fintrack.dto.dashboardDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseByCategoryDTO {
    private String categoryName;
    private BigDecimal amount;
    private String color;
}
