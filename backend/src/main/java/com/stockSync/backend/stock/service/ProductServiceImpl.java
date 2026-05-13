package com.stockSync.backend.stock.service;

import com.stockSync.backend.stock.dto.ProductRequest;
import com.stockSync.backend.stock.dto.ProductRequest.WarehouseEntry;
import com.stockSync.backend.stock.dto.ProductResponse;
import com.stockSync.backend.stock.dto.ProductResponse.WarehouseStockInfo;
import com.stockSync.backend.stock.mapper.ProductMapper;
import com.stockSync.backend.stock.model.Category;
import com.stockSync.backend.stock.model.Product;
import com.stockSync.backend.stock.model.Stock;
import com.stockSync.backend.stock.model.Warehouse;
import com.stockSync.backend.stock.repository.CategoryRepository;
import com.stockSync.backend.stock.repository.ProductRepository;
import com.stockSync.backend.stock.repository.StockRepository;
import com.stockSync.backend.stock.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockRepository stockRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponse> responses = productMapper.toResponseList(products);
        populateWarehouseStocks(responses);
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto con ID: " + id + " no encontrado"));
        ProductResponse response = productMapper.toResponse(product);
        List<Stock> stocks = stockRepository.findByProductId(id);
        response.setWarehouseStocks(toWarehouseStockInfo(stocks));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProductsByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        List<ProductResponse> responses = productMapper.toResponseList(products);
        populateWarehouseStocks(responses);
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Categoria no encontrada");
        }
        List<Product> products = productRepository.findByCategoryId(categoryId);
        List<ProductResponse> responses = productMapper.toResponseList(products);
        populateWarehouseStocks(responses);
        return responses;
    }

    @Override
    public long countAllProducts() {
        return productRepository.count();
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new RuntimeException("El SKU " + request.getSku() + " ya existe");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada."));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        long totalStock = 0;
        if (request.getWarehouseStocks() != null) {
            totalStock = request.getWarehouseStocks().stream()
                    .mapToLong(WarehouseEntry::getQuantity)
                    .sum();
        }
        product.setStock(totalStock);

        product = productRepository.save(product);

        if (request.getWarehouseStocks() != null) {
            for (WarehouseEntry entry : request.getWarehouseStocks()) {
                Warehouse warehouse = warehouseRepository.findById(entry.getWarehouseId())
                        .orElseThrow(() -> new RuntimeException("Bodega con ID: " + entry.getWarehouseId() + " no encontrada"));
                Stock stock = new Stock(product, warehouse, entry.getQuantity());
                stockRepository.save(stock);
            }
        }

        ProductResponse response = productMapper.toResponse(product);
        List<Stock> savedStocks = stockRepository.findByProductId(product.getId());
        response.setWarehouseStocks(toWarehouseStockInfo(savedStocks));
        return response;
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto con ID: " + id + " no encontrado"));

        productMapper.updateEntityFromRequest(request, product);

        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            Category newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Nueva Categoria no encontrada."));
            product.setCategory(newCategory);
        }

        product = productRepository.save(product);

        ProductResponse response = productMapper.toResponse(product);
        List<Stock> stocks = stockRepository.findByProductId(id);
        response.setWarehouseStocks(toWarehouseStockInfo(stocks));
        return response;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Producto no encontrado");
        }
        productRepository.deleteById(id);
    }

    private void populateWarehouseStocks(List<ProductResponse> responses) {
        if (responses.isEmpty()) return;

        List<Long> productIds = responses.stream()
                .map(ProductResponse::getId)
                .collect(Collectors.toList());

        List<Stock> allStocks = stockRepository.findAll().stream()
                .filter(s -> productIds.contains(s.getProduct().getId()))
                .collect(Collectors.toList());

        Map<Long, List<Stock>> stocksByProduct = allStocks.stream()
                .collect(Collectors.groupingBy(s -> s.getProduct().getId()));

        for (ProductResponse response : responses) {
            List<Stock> productStocks = stocksByProduct.getOrDefault(response.getId(), Collections.emptyList());
            response.setWarehouseStocks(toWarehouseStockInfo(productStocks));
        }
    }

    private List<WarehouseStockInfo> toWarehouseStockInfo(List<Stock> stocks) {
        return stocks.stream()
                .map(s -> new WarehouseStockInfo(
                        s.getWarehouse().getId(),
                        s.getWarehouse().getName(),
                        s.getQuantity()
                ))
                .collect(Collectors.toList());
    }
}
