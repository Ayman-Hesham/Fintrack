package com.fintrack.fintrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.fintrack.fintrack.dto.categoryDTO.*;
import com.fintrack.fintrack.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "custom", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toEntity(CreateCategoryRequest req);
}
 