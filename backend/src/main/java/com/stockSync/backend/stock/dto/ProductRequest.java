package com.stockSync.backend.stock.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "El nombre del producto no puede estar vacio")
    private String name;

    private String description;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal price;

    private String sku;

    private String imageUrl;

    private Boolean active;

    @NotNull(message = "La categoria es obligatoria")
    private Long categoryId;

    private List<WarehouseEntry> warehouseStocks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseEntry {
        @NotNull
        private Long warehouseId;

        @NotNull
        private Integer quantity;
    }
}
// Notas :
//1. categoryId (Long): Solo enviamos el ID de la categoría en el JSON de entrada, no todo el objeto Category. Luego en el Servicio (Fase 5) buscaremos la entidad Category usando este ID.
//2. active: Es Boolean (objeto), no boolean primitivo, para permitir que sea null si el cliente no lo envía (aunque en la entidad tiene un default true).
//3. Validaciones: @NotNull para campos obligatorios y @NotBlank para cadenas que no deben estar vacías.

