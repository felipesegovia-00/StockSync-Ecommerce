package com.stockSync.backend.stock.service;

import com.stockSync.backend.common.exception.BadRequestException;
import com.stockSync.backend.common.exception.ResourceNotFoundException;
import com.stockSync.backend.stock.dto.StockRequestCreateDto;
import com.stockSync.backend.stock.dto.StockRequestResponse;
import com.stockSync.backend.stock.mapper.StockRequestMapper;
import com.stockSync.backend.stock.model.Product;
import com.stockSync.backend.stock.model.Stock;
import com.stockSync.backend.stock.model.StockMovement;
import com.stockSync.backend.stock.model.StockRequest;
import com.stockSync.backend.stock.model.Warehouse;
import com.stockSync.backend.stock.repository.ProductRepository;
import com.stockSync.backend.stock.repository.StockMovementRepository;
import com.stockSync.backend.stock.repository.StockRepository;
import com.stockSync.backend.stock.repository.StockRequestRepository;
import com.stockSync.backend.stock.repository.WarehouseRepository;
import com.stockSync.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockRequestServiceImpl extends BaseService implements StockRequestService {

    private final StockRequestRepository stockRequestRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockRequestMapper stockRequestMapper;
    private final StockRepository stockRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    @Transactional
    public StockRequestResponse createRequest(StockRequestCreateDto dto) {
        User currentUser = getTenantUser();
        
        // Verificar que el trabajador pide para su bodega asignada, a menos que sea ADMIN
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        if (!isAdmin && currentUser.getAssignedWarehouse() != null && !currentUser.getAssignedWarehouse().getId().equals(dto.getDestinationWarehouseId())) {
            throw new AccessDeniedException("Solo puedes solicitar stock para tu bodega asignada.");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", dto.getProductId()));
        Warehouse sourceWh = warehouseRepository.findById(dto.getSourceWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega", "id", dto.getSourceWarehouseId()));
        Warehouse destWh = warehouseRepository.findById(dto.getDestinationWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodega", "id", dto.getDestinationWarehouseId()));

        StockRequest request = StockRequest.builder()
                .product(product)
                .sourceWarehouse(sourceWh)
                .destinationWarehouse(destWh)
                .quantity(dto.getQuantity())
                .status("PENDIENTE")
                .build();

        return stockRequestMapper.toResponse(stockRequestRepository.save(request));
    }

    @Override
    @Transactional
    public List<StockRequestResponse> createRequestsBatch(List<StockRequestCreateDto> dtos) {
        Map<Long, List<StockRequestCreateDto>> groupedBySource = dtos.stream()
                .collect(Collectors.groupingBy(StockRequestCreateDto::getSourceWarehouseId));

        List<StockRequestResponse> responses = new ArrayList<>();
        for (Map.Entry<Long, List<StockRequestCreateDto>> entry : groupedBySource.entrySet()) {
            String groupCode = "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            for (StockRequestCreateDto dto : entry.getValue()) {
                Warehouse sourceWh = warehouseRepository.findById(dto.getSourceWarehouseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", dto.getSourceWarehouseId()));
                Warehouse destWh = warehouseRepository.findById(dto.getDestinationWarehouseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", dto.getDestinationWarehouseId()));
                Product product = productRepository.findById(dto.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", dto.getProductId()));

                StockRequest request = StockRequest.builder()
                        .product(product)
                        .sourceWarehouse(sourceWh)
                        .destinationWarehouse(destWh)
                        .quantity(dto.getQuantity())
                        .status("PENDIENTE")
                        .requestGroupCode(groupCode)
                        .build();

                stockRequestRepository.save(request);
                responses.add(stockRequestMapper.toResponse(request));
            }
        }
        return responses;
    }

    @Override
    public List<StockRequestResponse> getRequestsByDestination(Long destinationId) {
        return stockRequestMapper.toResponseList(stockRequestRepository.findByDestinationWarehouseId(destinationId));
    }

    @Override
    public List<StockRequestResponse> getRequestsBySource(Long sourceId) {
        return stockRequestMapper.toResponseList(stockRequestRepository.findBySourceWarehouseId(sourceId));
    }

    @Override
    public List<StockRequestResponse> getAllRequests() {
        return stockRequestMapper.toResponseList(stockRequestRepository.findAll());
    }

    @Override
    @Transactional
    public StockRequestResponse updateStatus(Long requestId, String status) {
        StockRequest request = stockRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("StockRequest", "id", requestId));
        
        request.setStatus(status);
        
        if ("ENVIADO".equals(status) && request.getTrackingSku() == null) {
            String tracking = "ENV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            request.setTrackingSku(tracking);
        }
        
        return stockRequestMapper.toResponse(stockRequestRepository.save(request));
    }

    @Override
    @Transactional
    public StockRequestResponse receiveByScanner(String trackingSku, Long destinationWarehouseId) {
        User currentUser = getTenantUser();
        
        if (currentUser.getAssignedWarehouse() != null && !currentUser.getAssignedWarehouse().getId().equals(destinationWarehouseId)) {
            throw new AccessDeniedException("No puedes recepcionar en una bodega que no tienes asignada.");
        }

        // Buscar una solicitud en estado ENVIADO (SHIPPED)
        List<StockRequest> requests = stockRequestRepository.findByTrackingSkuAndStatus(trackingSku, "ENVIADO");
        StockRequest targetRequest = requests.stream()
                .filter(r -> r.getDestinationWarehouse().getId().equals(destinationWarehouseId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No hay envíos pendientes para el SKU " + trackingSku + " en este local."));

        // Procesar la recepción: cambiar a RECIBIDO y mover stock
        targetRequest.setStatus("RECIBIDO");
        stockRequestRepository.save(targetRequest);

        Stock sourceStock = stockRepository.findByProductIdAndWarehouseIdAndUserId(
                        targetRequest.getProduct().getId(), targetRequest.getSourceWarehouse().getId(), getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("No hay existencias en bodega de origen."));

        if (sourceStock.getQuantity() < targetRequest.getQuantity()) {
            throw new BadRequestException("Stock insuficiente en bodega de origen al recepcionar.");
        }

        Stock destStock = stockRepository.findByProductIdAndWarehouseIdAndUserId(
                        targetRequest.getProduct().getId(), targetRequest.getDestinationWarehouse().getId(), getTenantId())
                .orElse(new Stock(targetRequest.getProduct(), targetRequest.getDestinationWarehouse(), 0));

        sourceStock.setQuantity(sourceStock.getQuantity() - targetRequest.getQuantity());
        destStock.setQuantity(destStock.getQuantity() + targetRequest.getQuantity());
        destStock.setUser(getTenantUser());

        stockRepository.save(sourceStock);
        stockRepository.save(destStock);

        StockMovement movement = StockMovement.builder()
                .product(targetRequest.getProduct())
                .sourceWarehouse(targetRequest.getSourceWarehouse())
                .destinationWarehouse(targetRequest.getDestinationWarehouse())
                .quantity(targetRequest.getQuantity())
                .movementType("TRASLADO")
                .build();
        stockMovementRepository.save(movement);

        return stockRequestMapper.toResponse(targetRequest);
    }
}
