package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.dto.StockRequestCreateDto;
import com.stockSync.backend.stock.dto.StockRequestResponse;
import java.util.List;

public interface StockRequestService {
    StockRequestResponse createRequest(StockRequestCreateDto dto);
    List<StockRequestResponse> getRequestsByDestination(Long destinationId);
    List<StockRequestResponse> getRequestsBySource(Long sourceId);
    List<StockRequestResponse> getAllRequests();
    StockRequestResponse updateStatus(Long requestId, String status);
    StockRequestResponse receiveByScanner(String trackingSku, Long destinationWarehouseId);
    List<StockRequestResponse> createRequestsBatch(List<StockRequestCreateDto> dtos);
}
