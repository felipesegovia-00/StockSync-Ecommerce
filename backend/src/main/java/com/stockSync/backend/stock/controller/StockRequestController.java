package com.stockSync.backend.stock.controller;

import com.stockSync.backend.stock.dto.StockRequestCreateDto;
import com.stockSync.backend.stock.dto.StockRequestResponse;
import com.stockSync.backend.stock.service.StockRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stock-requests")
@RequiredArgsConstructor
public class StockRequestController {

    private final StockRequestService stockRequestService;

    @PostMapping
    public ResponseEntity<StockRequestResponse> createRequest(@RequestBody StockRequestCreateDto requestDto) {
        return ResponseEntity.ok(stockRequestService.createRequest(requestDto));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<StockRequestResponse>> createRequestsBatch(@RequestBody List<StockRequestCreateDto> dtos) {
        return ResponseEntity.ok(stockRequestService.createRequestsBatch(dtos));
    }

    @GetMapping
    public ResponseEntity<List<StockRequestResponse>> getAllRequests() {
        return ResponseEntity.ok(stockRequestService.getAllRequests());
    }

    @GetMapping("/destination/{warehouseId}")
    public ResponseEntity<List<StockRequestResponse>> getByDestination(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockRequestService.getRequestsByDestination(warehouseId));
    }

    @GetMapping("/source/{warehouseId}")
    public ResponseEntity<List<StockRequestResponse>> getBySource(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(stockRequestService.getRequestsBySource(warehouseId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<StockRequestResponse> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(stockRequestService.updateStatus(id, body.get("status")));
    }

    @PostMapping("/receive")
    public ResponseEntity<StockRequestResponse> receiveByScanner(@RequestBody Map<String, Object> body) {
        String sku = (String) body.get("sku");
        Long destinationWarehouseId = Long.valueOf(body.get("destinationWarehouseId").toString());
        return ResponseEntity.ok(stockRequestService.receiveByScanner(sku, destinationWarehouseId));
    }
}
