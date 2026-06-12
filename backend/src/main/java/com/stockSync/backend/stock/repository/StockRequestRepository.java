package com.stockSync.backend.stock.repository;

import com.stockSync.backend.stock.model.StockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRequestRepository extends JpaRepository<StockRequest, Long> {
    List<StockRequest> findByDestinationWarehouseId(Long destinationWarehouseId);
    List<StockRequest> findBySourceWarehouseId(Long sourceWarehouseId);
    
    // Find active requests by SKU
    List<StockRequest> findByProductSkuAndStatus(String sku, String status);
    
    // Find active requests by tracking SKU
    List<StockRequest> findByTrackingSkuAndStatus(String trackingSku, String status);
}
