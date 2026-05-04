package com.stockSync.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

//Notas :
//1. Este DTO es para la salida (lo que devuelve el API al frontend).
//2. Incluye id y las fechas (createdAt, updatedAt) para que el cliente tenga toda la información de la categoría.
//3. No lleva anotaciones de validación (@NotBlank, etc.) porque es un objeto de respuesta, no de entrada.
