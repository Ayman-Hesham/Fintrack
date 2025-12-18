package com.fintrack.fintrack.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.service.CategoryService;
import com.fintrack.fintrack.dto.categoryDTO.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public ResponseEntity<List<CategoryResponse>> getAllCategoriesByUserId(@AuthenticationPrincipal User user) {
        List<CategoryResponse> res = categoryService.getAllCategoriesByUserId(user);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest dto,
            @AuthenticationPrincipal User user) {
        CategoryResponse res = categoryService.createCategory(dto, user);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@Valid @RequestBody CreateCategoryRequest dto,
            @PathVariable Long categoryId) {
        CategoryResponse res = categoryService.updateCategory(dto, categoryId);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
