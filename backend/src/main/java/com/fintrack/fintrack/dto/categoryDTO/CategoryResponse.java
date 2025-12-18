package com.fintrack.fintrack.dto.categoryDTO;

import com.fintrack.fintrack.model.CategoryColor;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String icon;
    private CategoryColor color;
    @JsonProperty("isCustom")
    private boolean isCustom;
}
