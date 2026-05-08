package com.stockSync.backend.stock.repository;

import com.stockSync.backend.stock.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    boolean existsByCode(String code);



}
