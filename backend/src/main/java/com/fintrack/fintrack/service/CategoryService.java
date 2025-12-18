package com.fintrack.fintrack.service;

import org.springframework.stereotype.Service;

import com.fintrack.fintrack.repository.CategoryRepository;
import com.fintrack.fintrack.dto.categoryDTO.*;
import com.fintrack.fintrack.mapper.CategoryMapper;
import com.fintrack.fintrack.model.Category;
import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.exception.BadRequestException;
import com.fintrack.fintrack.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryResponse> getAllCategoriesByUserId(User user) {
        List<Category> categories = categoryRepository.findForUserIncludingDefault(user.getId());
        return categories.stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    public List<Category> getCategoriesForUser(User user) {
        return categoryRepository.findForUserIncludingDefault(user.getId());
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    public CategoryResponse createCategory(CreateCategoryRequest dto, User user) {
        Category category = categoryMapper.toEntity(dto);
        if (categoryRepository.findByNameAndUser(dto.getName(), user.getId()).isPresent()) {
            throw new BadRequestException("Category already exists with name: " + dto.getName());
        }
        category.setUser(user);
        category.setCustom(true);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(savedCategory);
    }

    public CategoryResponse updateCategory(CreateCategoryRequest dto, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        if (categoryRepository.findByNameAndUser(dto.getName(), category.getUser().getId()).isPresent()) {
            throw new BadRequestException("Category already exists with name: " + dto.getName());
        }
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setColor(dto.getColor());
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(savedCategory);
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public Category getCategoryByNameAndUser(String name, User user) {
        return categoryRepository.findByNameAndUser(name, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));
    }
}
