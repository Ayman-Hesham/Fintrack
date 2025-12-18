package com.fintrack.fintrack.dto.categoryDTO;

import com.fintrack.fintrack.model.CategoryColor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest {
    @NotBlank(message = "Category name is required")
    @Size(min = 3, max = 30, message = "Category name must be between 3 and 30 characters")
    private String name;

    @NotBlank(message = "Category icon is required")
    @Size(min = 1, max = 10, message = "Icon must be between 1 and 10 characters")
    private String icon;

    @NotNull(message = "Category color is required")
    private CategoryColor color;
}
