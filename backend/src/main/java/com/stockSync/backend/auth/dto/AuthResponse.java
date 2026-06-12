package com.stockSync.backend.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@NullMarked
public class AuthResponse {

    private String token;
    private String email;
    private String nombre;
    private String role;
    private boolean forcePasswordChange;
    private String companyName;
    private String companyLogo;

    private WarehouseDto assignedWarehouse;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WarehouseDto {
        private Long id;
        private String name;
    }
}
