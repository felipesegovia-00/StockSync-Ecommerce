package com.stockSync.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "El nombre de la categoria no puede estar vacio")
    private String name;

    private String description;
}
//Notas :
//1. Crea primero el paquete dto dentro de com.stockSync.backend.
//2. Usa jakarta.validation.constraints.NotBlank (viene con spring-boot-starter-validation).
//3. Este DTO se usará para recibir los datos cuando el cliente (frontend) envíe un JSON para crear o actualizar una categoría.
