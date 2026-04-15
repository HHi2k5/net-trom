package com.webtruyen.backend.service;

import com.webtruyen.backend.exception.ResourceNotFoundException;
import com.webtruyen.backend.model.Category;
import com.webtruyen.backend.repository.CategoryRepository;
import com.webtruyen.backend.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
    }

    public Category createCategory(Category category) {
        if (category.getSlug() == null || category.getSlug().isEmpty()) {
            category.setSlug(generateUniqueSlug(category.getName()));
        }
        return categoryRepository.save(category);
    }

    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.makeSlug(name);
        String slug = baseSlug;
        int count = 1;
        while (categoryRepository.findBySlug(slug).isPresent()) {
            slug = baseSlug + "-" + count++;
        }
        return slug;
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);
        category.setName(categoryDetails.getName());
        
        // If slug is explicitly provided, use it. Otherwise, if it's currently missing, generate one.
        if (categoryDetails.getSlug() != null && !categoryDetails.getSlug().isEmpty()) {
            category.setSlug(categoryDetails.getSlug());
        } else if (category.getSlug() == null || category.getSlug().isEmpty()) {
            category.setSlug(generateUniqueSlug(category.getName()));
        }

        category.setDescription(categoryDetails.getDescription());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
