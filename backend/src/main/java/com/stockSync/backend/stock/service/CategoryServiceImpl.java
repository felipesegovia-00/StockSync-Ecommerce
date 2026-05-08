package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.dto.CategoryRequest;
import com.stockSync.backend.stock.dto.CategoryResponse;
import com.stockSync.backend.stock.mapper.CategoryMapper;
import com.stockSync.backend.stock.model.Category;
import com.stockSync.backend.stock.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

        private final CategoryRepository categoryRepository;
        private final CategoryMapper categoryMapper;


    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toResponseList(categoryRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category =  categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("La categoria '" + request.getName() + "' ya existe");
        }
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se pudo actualizar: Categoria no encontrada"));
        categoryMapper.updateEntityFromRequest(request, category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }



    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se pudo eliminar: Categoria no encontrada"));

        if (!category.getProducts().isEmpty()) {
            throw new RuntimeException("No se puede eliminar: La categoria tiene productos. ");
        }

        categoryRepository.delete(category);
    }
}
