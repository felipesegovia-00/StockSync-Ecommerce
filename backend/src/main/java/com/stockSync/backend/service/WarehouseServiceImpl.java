package com.stockSync.backend.service;

import com.stockSync.backend.dto.WarehouseRequest;
import com.stockSync.backend.dto.WarehouseResponse;
import com.stockSync.backend.entity.Warehouse;
import com.stockSync.backend.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;


    @Override
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        if (warehouseRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("El codigo de bodega:" + request.getCode() + " ya existe");
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setCode(request.getCode());
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setCity(request.getCity());

        Warehouse saved =  warehouseRepository.save(warehouse);

        return mapToResponse(saved);
    }

    private WarehouseResponse mapToResponse(Warehouse entity) {
        WarehouseResponse response = new WarehouseResponse();
        response.setId(entity.getId());
        response.setCode(entity.getCode());
        response.setName(entity.getName());
        response.setAddress(entity.getAddress());
        response.setCity(entity.getCity());
        response.setCreateAt(entity.getCreateAt().toString());
        return response;

    }

    @Override
    public List<WarehouseResponse> getAllWarehouse() {

        return warehouseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public WarehouseResponse getWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Bodega no encontrada"));
    }

    @Override
    @Transactional
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id).
                orElseThrow(() -> new RuntimeException("No se puede actualizar: ID no encontrado"));

                warehouse.setCode(request.getCode());
                warehouse.setName(request.getName());
                warehouse.setAddress(request.getAddress());
                warehouse.setCity(request.getCity());

                return mapToResponse(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {

    }

}
