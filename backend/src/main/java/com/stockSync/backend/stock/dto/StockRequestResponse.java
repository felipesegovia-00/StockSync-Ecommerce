package com.stockSync.backend.stock.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StockRequestResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String sku;
    private Long sourceWarehouseId;
    private String sourceWarehouseName;
    private Long destinationWarehouseId;
    private String destinationWarehouseName;
    private Integer quantity;
    private String status;
    private String trackingSku;
    private String requestGroupCode;
    private LocalDateTime createdAt;
}
