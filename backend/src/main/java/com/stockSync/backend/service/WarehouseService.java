package com.stockSync.backend.service;

import com.stockSync.backend.entity.Warehouse;
import com.stockSync.backend.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository){
        this.warehouseRepository = warehouseRepository;
    }

    public Warehouse createWarehouse(Warehouse warehouse){
        if (warehouseRepository.existsByCode(warehouse.getCode())){
            throw new RuntimeException("Esta bodega ya existe");
        }

        return warehouseRepository.save(warehouse);
    }

    public List<Warehouse> getAllWarehouse(){
        return  warehouseRepository.findAll();
    }
}
