package com.stockSync.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String sku;
    private String imageUrl;
    private Boolean active;

    private Long categoryId;
    private String categoryName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
//Notas para ti:
//1. categoryName: Se incluye para que el frontend pueda mostrar el nombre de la categoría sin tener que hacer una consulta adicional.
//2. Sin validaciones: Al ser un DTO de respuesta, no lleva anotaciones de validación (@NotNull, etc.).
//3. Tipos de datos: Asegúrate de que coincidan con la Entidad (BigDecimal para precio, Boolean para active).
