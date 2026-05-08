package com.stockSync.backend.stock.controller;


import com.stockSync.backend.stock.dto.WarehouseRequest;
import com.stockSync.backend.stock.dto.WarehouseResponse;
import com.stockSync.backend.stock.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;


    @GetMapping // CONSULTA TODAS LAS BODEGAS
    public ResponseEntity<List<WarehouseResponse>> getAllWarehouses(){
        //SI EXISTE UN ERROR EN LA BD, GLOBALHANDLER RESPONDE 500
    return ResponseEntity.ok(warehouseService.getAllWarehouse());
    }

    @GetMapping("/{id}") //CONSULTAS DE BODEGAS POR ID
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id){
       // SI NO EXISTE EL CODIGO, EL SERVICIO LANZA RUNTIMEEXCEPTION Y HANDLER RESPONDE 404
            return ResponseEntity.ok(warehouseService.getWarehouseById(id));

    }

    @PostMapping //CREA LAS BODEGAS
    public ResponseEntity<WarehouseResponse> createWarehouse(@RequestBody WarehouseRequest request){
        //SI EL CODIGO EXISTE, EL SERVICIO LANZA EXCEPCION Y EL HANDLER RESPONDE
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.createWarehouse(request));
    }

    @PutMapping("/{id}") //ACTUALIZA DATOS DE LAS BODEGAS
    public ResponseEntity<WarehouseResponse> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseRequest request){
        //LOGICA DE "NO ENCONTRADO" ESTA EN  EL SERVICE
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, request));

    }

    @DeleteMapping("/{id}") //ELIMINA BODEGAS
    public ResponseEntity<Void>  deleteWarehouse(@PathVariable Long id){
       //EL SERVICIO VERIFICA QUE EXISTA. SI NO EXISTE, EL HANDLER ENVIA EL ERROR 404
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
        }

}