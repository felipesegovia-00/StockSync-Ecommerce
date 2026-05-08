package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.dto.ProductRequest;
import com.stockSync.backend.stock.dto.ProductResponse;
import com.stockSync.backend.stock.mapper.ProductMapper;
import com.stockSync.backend.stock.model.Category;
import com.stockSync.backend.stock.model.Product;
import com.stockSync.backend.stock.repository.CategoryRepository;
import com.stockSync.backend.stock.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productMapper.toResponseList(productRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto con ID: " + id + " no encontrado"));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        //Verificamos si la categoria existe antes de buscar
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Categoria no encontrada");
        }
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        //Validar si el SKU ya existe para evitar duplicados
        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new RuntimeException("El SKU " + request.getSku() + " ya existe");
        }

        //Buscar la categoria obligatoria
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada."));

        //Convertir DTO a Entidad y asigna Categoria
        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        //Guardar y devolver respuesta del Mapper
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto con ID: " + id + " no encontrado"));

        //Actualiza campos basicos usando MapStruct
        productMapper.updateEntityFromRequest(request, product);

        //Si cambio la categoria, se actualiza
        if(!product.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Nueva Categoria no encontrada."));
            product.setCategory(newCategory);
        }

        return  productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Producto no encontrado");
        }
        productRepository.deleteById(id);
    }
}
