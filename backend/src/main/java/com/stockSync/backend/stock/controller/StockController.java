package com.stockSync.backend.stock.controller;

import com.stockSync.backend.stock.dto.StockRequest;
import com.stockSync.backend.stock.dto.StockResponse;
import com.stockSync.backend.stock.dto.StockTransferRequest;
import com.stockSync.backend.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    //Lista todo el inventario global
    @GetMapping
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    //Ver stocks por bodega especifica
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<StockResponse>> getByWarehouse(@PathVariable Long warehouseId){
        return ResponseEntity.ok(stockService.getStocksByWarehouse(warehouseId));
    }

    //Ver stock por producto especifico
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<StockResponse>> getByProduct(@PathVariable Long productId){
        return ResponseEntity.ok(stockService.getStocksByProduct(productId));
    }

    //Agregar stock (suma a lo existente)
    @PostMapping("/add")
    public ResponseEntity<StockResponse> addStock(@RequestBody StockRequest stockRequest){
        return new ResponseEntity<>(stockService.addStock(stockRequest), HttpStatus.CREATED);
    }

    //Ajustar Stock (sobreescribe el valor - correciones manuales)
    @PutMapping("/update")
    public ResponseEntity<StockResponse> updateStock(@RequestBody StockRequest stockRequest){
        return ResponseEntity.ok(stockService.updateStock(stockRequest));
        }

    //Tranferencia entre bodegas (Logica de historial incluida)
    @PostMapping("/transfer")
    public ResponseEntity<Void> transferStock(@RequestBody StockTransferRequest request) {
        stockService.transferStock(request);
        return ResponseEntity.noContent().build(); //204: Exito sin cuerpo de respuesta
    }

    //Eliminar registro de stock por id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable Long id){
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }





}
