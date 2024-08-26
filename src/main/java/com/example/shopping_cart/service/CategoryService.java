package com.example.shopping_cart.service;

import com.example.shopping_cart.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    public Category saveCategory(Category category);

    public Boolean exitCategory(String name);

    public List<Category> getAllCategories();

    // xóa cate gory
    public Boolean deleteCategory(int id);

    // lấy giá trị category theo id
    public Category getCategoryId(int id);
    // hoạt động
    public List<Category> getAllActiveCategory();
    public Page<Category> getAllCategoryPagination(Integer page, Integer size);

}
