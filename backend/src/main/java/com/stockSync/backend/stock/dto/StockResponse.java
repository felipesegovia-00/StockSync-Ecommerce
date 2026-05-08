package com.stockSync.backend.stock.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockResponse {
    private Long id;
    private String productName;
    private String warehouseName;
    private Integer quantity;
    private LocalDateTime lastUpdate;
}
