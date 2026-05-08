package com.stockSync.backend.stock.mapper;


import com.stockSync.backend.stock.dto.WarehouseRequest;
import com.stockSync.backend.stock.dto.WarehouseResponse;
import com.stockSync.backend.stock.model.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    //DTO a Entidad (para crear nuevas bodegas)
    //Ignoramos el ID porque lo genera la DB en Neon
    //CREATE: El request se convierte en una nueva Entidad
    @Mapping(target = "id", ignore = true)
    Warehouse toEntity(WarehouseRequest request);

    //READ: Convierte la entidad en uan respuesta para el cliente
    WarehouseResponse toResponse(Warehouse entity);

    //UPDATE: Actualiza una entidad que ya existe con los datos del Request
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(WarehouseRequest request, @MappingTarget Warehouse entity);

    //LIST: Convierte las listas de entidades a listas de DTOs
    List<WarehouseResponse> toResponseList(List<Warehouse> entities);
}
