package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.dto.ProductRequest;
import com.stockSync.backend.stock.dto.ProductResponse;
import java.util.List;



public interface ProductService {

    //Lectura
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    List<ProductResponse> searchProductsByName(String name);
    List<ProductResponse> getProductsByCategoryId(Long categoryId);


    //Escritura
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);



}
