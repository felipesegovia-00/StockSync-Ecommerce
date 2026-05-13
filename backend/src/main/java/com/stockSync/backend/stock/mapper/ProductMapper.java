package com.stockSync.backend.stock.mapper;

import com.stockSync.backend.stock.dto.ProductRequest;
import com.stockSync.backend.stock.dto.ProductResponse;
import com.stockSync.backend.stock.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "warehouseStocks", ignore = true)
    ProductResponse toResponse(Product entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", source = "active", defaultValue = "true")
    @Mapping(target = "stock", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "stock", ignore = true)
    void updateEntityFromRequest(ProductRequest request, @MappingTarget Product entity);

    List<ProductResponse> toResponseList(List<Product> entities);
}
