package com.example.shopping_cart.service.impl;

import com.example.shopping_cart.model.Category;
import com.example.shopping_cart.repository.CategoryRepository;
import com.example.shopping_cart.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Boolean exitCategory(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Boolean deleteCategory(int id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            categoryRepository.delete(category);
            return true;
        }
        return false;


    }

    @Override
    public Category getCategoryId(int id) {
        Category category = categoryRepository.findById(id).orElse(null);

        return category;
    }

    @Override
    public List<Category> getAllActiveCategory() {
        List<Category> categories=  categoryRepository.findByIsActiveTrue();
        return categories;
    }

    @Override
    public Page<Category> getAllCategoryPagination(Integer page,Integer size) {
        Pageable pageable = PageRequest.of(page,size);

        return categoryRepository.findAll(pageable);
    }


}
