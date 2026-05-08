package com.stockSync.backend.stock.service;


import com.stockSync.backend.stock.dto.CategoryRequest;
import com.stockSync.backend.stock.dto.CategoryResponse;



import java.util.List;


public interface CategoryService {

    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}



