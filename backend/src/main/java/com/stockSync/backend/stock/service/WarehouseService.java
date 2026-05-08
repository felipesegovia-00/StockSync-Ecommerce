package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.dto.WarehouseRequest;
import com.stockSync.backend.stock.dto.WarehouseResponse;

import java.util.List;


public interface WarehouseService {

    WarehouseResponse createWarehouse(WarehouseRequest request);
    List<WarehouseResponse> getAllWarehouse();
    WarehouseResponse getWarehouseById(Long id);
    WarehouseResponse updateWarehouse(Long id, WarehouseRequest request);
    void deleteWarehouse(Long id);

}
