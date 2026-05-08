package com.stockSync.backend.stock.repository;


import com.stockSync.backend.stock.model.Product;
import com.stockSync.backend.stock.model.Stock;
import com.stockSync.backend.stock.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findByWarehouseId(Long warehouseId);
    List<Stock> findByProductId(Long productId);
    Optional<Stock> findByProductAndWarehouse(Product product, Warehouse warehouse);
    boolean existsByProductIdAndWarehouseId(Long productId, Long warehouseId);
    Optional<Stock> findByProductIdAndWarehouseId(Long productId, Long warehouseId);
}
