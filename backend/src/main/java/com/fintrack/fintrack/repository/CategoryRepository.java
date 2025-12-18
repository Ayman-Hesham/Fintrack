package com.fintrack.fintrack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fintrack.fintrack.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE (c.user.id = :userId) OR c.isCustom = false")
    List<Category> findForUserIncludingDefault(@Param("userId") Long userId);

    @Query("SELECT c FROM Category c WHERE c.name = :name AND ((c.user.id = :userId) OR c.isCustom = false)")
    Optional<Category> findByNameAndUser(@Param("name") String name, @Param("userId") Long userId);
}
