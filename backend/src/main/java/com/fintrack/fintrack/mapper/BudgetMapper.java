package com.fintrack.fintrack.mapper;

import com.fintrack.fintrack.dto.BudgetDTO.*;
import com.fintrack.fintrack.model.Budget;
import com.fintrack.fintrack.model.Category;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import java.math.BigDecimal;


@Mapper(componentModel = "spring")
public interface BudgetMapper {
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.icon", target = "categoryIcon")
    @Mapping(source = "budget.id", target = "id")
    BudgetResponse toBudgetResponse(Budget budget, BigDecimal spentAmount, Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Budget toBudget(CreateBudgetRequest createBudgetRequest);
}
