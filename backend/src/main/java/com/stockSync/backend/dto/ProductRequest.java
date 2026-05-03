package com.stockSync.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El Stock no puede ser negativo")
    private Integer stock;

    private String sku;

    private String imageUrl;

    private Boolean active;

    @NotNull(message = "La categoria es obligatoria")
    private Long categoryId;

}
// Notas :
//1. categoryId (Long): Solo enviamos el ID de la categoría en el JSON de entrada, no todo el objeto Category. Luego en el Servicio (Fase 5) buscaremos la entidad Category usando este ID.
//2. active: Es Boolean (objeto), no boolean primitivo, para permitir que sea null si el cliente no lo envía (aunque en la entidad tiene un default true).
//3. Validaciones: @NotNull para campos obligatorios y @NotBlank para cadenas que no deben estar vacías.

