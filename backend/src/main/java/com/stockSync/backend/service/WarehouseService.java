package com.stockSync.backend.service;

import com.stockSync.backend.dto.WarehouseRequest;
import com.stockSync.backend.dto.WarehouseResponse;
import com.stockSync.backend.entity.Warehouse;
import com.stockSync.backend.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;


public interface WarehouseService {

    WarehouseResponse createWarehouse(WarehouseRequest request);
    List<WarehouseResponse> getAllWarehouse();
    WarehouseResponse getWarehouseById(Long id);
    WarehouseResponse updateWarehouse(Long id, WarehouseRequest request);
    void deleteWarehouse(Long id);

}
