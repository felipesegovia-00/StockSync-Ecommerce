package com.stockSync.backend.stock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long stock;
    private String sku;
    private String imageUrl;
    private Boolean active;
    private String categoryName;
    private List<WarehouseStockInfo> warehouseStocks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseStockInfo {
        private Long warehouseId;
        private String warehouseName;
        private Integer quantity;
    }
}
//Notas:
//1. categoryName: Se incluye para que el frontend pueda mostrar el nombre de la categoría sin tener que hacer una consulta adicional.
//2. Sin validaciones: Al ser un DTO de respuesta, no lleva anotaciones de validación (@NotNull, etc.).
//3. Tipos de datos: Asegúrate de que coincidan con la Entidad (BigDecimal para precio, Boolean para active).
