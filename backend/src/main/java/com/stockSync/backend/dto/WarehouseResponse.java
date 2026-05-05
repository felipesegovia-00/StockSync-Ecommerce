package com.stockSync.backend.dto;

import lombok.Data;

@Data
public class WarehouseResponse {
    private Long id;
    private String code;
    private String name;
    private String address;
    private String city;
    private String createAt;
}
