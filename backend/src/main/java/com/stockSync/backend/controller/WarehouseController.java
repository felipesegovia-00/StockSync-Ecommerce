package com.stockSync.backend.controller;


import com.stockSync.backend.dto.WarehouseRequest;
import com.stockSync.backend.dto.WarehouseResponse;
import com.stockSync.backend.entity.Warehouse;
import com.stockSync.backend.repository.WarehouseRepository;
import com.stockSync.backend.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;


    @GetMapping
    public ResponseEntity<List<WarehouseResponse>> getAllWarehouses(){
    return ResponseEntity.ok(warehouseService.getAllWarehouse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(warehouseService.getWarehouseById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseRequest request){
        try {
            return ResponseEntity.ok(warehouseService.updateWarehouse(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  deleteWarehouse(@PathVariable Long id){
        try {
            warehouseService.deleteWarehouse(id);
            return ResponseEntity.ok().build();
        }  catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}