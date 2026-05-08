package com.stockSync.backend.stock.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonPropertyOrder({ "id", "code", "name", "address", "city", "createAt" })
public class WarehouseRequest {
    @NotNull
    @NotBlank
    private String code;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @NotBlank
    private String address;
    @NotNull
    @NotBlank
    private String city;
}
