package com.stockSync.backend.stock.repository;


import com.stockSync.backend.stock.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //Buscar productos por nombre
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product>findByCategoryId(Long categoryId);

    Optional<Product> findBySku(String sku);

}
