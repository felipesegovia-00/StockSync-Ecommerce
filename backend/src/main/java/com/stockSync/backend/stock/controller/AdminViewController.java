package com.stockSync.backend.stock.controller;

import com.stockSync.backend.stock.dto.CategoryResponse;
import com.stockSync.backend.stock.dto.ProductResponse;
import com.stockSync.backend.stock.dto.StockResponse;
import com.stockSync.backend.stock.dto.WarehouseResponse;
import com.stockSync.backend.stock.service.CategoryService;
import com.stockSync.backend.stock.service.ProductService;
import com.stockSync.backend.stock.service.StockService;
import com.stockSync.backend.stock.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminViewController {

    private final ProductService productService;
    private final WarehouseService warehouseService;
    private final StockService stockService;
    private final CategoryService categoryService;

    @GetMapping("/productos")
    public ResponseEntity<List<ProductResponse>> productos() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/productos/nuevo")
    public ResponseEntity<Map<String, Object>> nuevoProductoForm() {
        return ResponseEntity.ok(Map.of("categorias", categoryService.getAllCategories()));
    }

    @GetMapping("/productos/editar/{id}")
    public ResponseEntity<Map<String, Object>> editarProductoForm(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
                "producto", productService.getProductById(id),
                "categorias", categoryService.getAllCategories()
        ));
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bodegas")
    public ResponseEntity<List<WarehouseResponse>> bodegas() {
        return ResponseEntity.ok(warehouseService.getAllWarehouse());
    }

    @DeleteMapping("/bodegas/{id}")
    public ResponseEntity<Void> eliminarBodega(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stock")
    public ResponseEntity<List<StockResponse>> stock() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @GetMapping("/stock/nuevo")
    public ResponseEntity<Map<String, Object>> nuevoStockForm() {
        return ResponseEntity.ok(Map.of(
                "productos", productService.getAllProducts(),
                "bodegas", warehouseService.getAllWarehouse()
        ));
    }

    @GetMapping("/stock/editar/{id}")
    public ResponseEntity<Map<String, Object>> editarStockForm(@PathVariable Long id) {
        StockResponse stock = stockService.getAllStocks().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow();
        return ResponseEntity.ok(Map.of(
                "stock", stock,
                "productos", productService.getAllProducts(),
                "bodegas", warehouseService.getAllWarehouse()
        ));
    }

    @DeleteMapping("/stock/{id}")
    public ResponseEntity<Void> eliminarStock(@PathVariable Long id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}
