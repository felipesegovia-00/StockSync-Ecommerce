package com.stockSync.backend.controller;


import com.stockSync.backend.entity.Warehouse;
import com.stockSync.backend.service.WarehouseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public  WarehouseController(WarehouseService warehouseService){
        this.warehouseService = warehouseService;
    }

    // CREAR UNA BODEGA
    @PostMapping
    public Warehouse create(@RequestBody Warehouse warehouse){
        return warehouseService.createWarehouse(warehouse);
    }

    // LISTAR TODAS LAS BODEGAS
    @GetMapping
    public List<Warehouse> getAll(){
        return  warehouseService.getAllWarehouse();
    }
}
