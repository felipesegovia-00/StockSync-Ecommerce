package com.stockSync.backend.stock.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({ "id", "code", "name", "address", "city", "createAt" })
public class WarehouseResponse {
    private Long id;
    private String code;
    private String name;
    private String address;
    private String city;
    private String createAt;
}
