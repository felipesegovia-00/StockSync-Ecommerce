package com.stockSync.backend.stock.service;

import com.stockSync.backend.common.exception.ResourceNotFoundException;
import com.stockSync.backend.stock.dto.ProductRequest;
import com.stockSync.backend.stock.dto.ProductResponse;
import com.stockSync.backend.stock.mapper.ProductMapper;
import com.stockSync.backend.stock.model.Category;
import com.stockSync.backend.stock.model.Product;
import com.stockSync.backend.stock.repository.CategoryRepository;
import com.stockSync.backend.stock.repository.ProductRepository;
import com.stockSync.backend.stock.repository.StockRepository;
import com.stockSync.backend.stock.repository.WarehouseRepository;
import com.stockSync.backend.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private User mockUser;

    @BeforeEach
    public void setup() {
        mockUser = new User();
        mockUser.setId(1L);

        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(mockUser);

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void givenExistingProductId_whenGetProductById_thenReturnProduct() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setUser(mockUser);

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(mockResponse);
        when(stockRepository.findByProductIdAndUserId(productId, mockUser.getId())).thenReturn(new ArrayList<>());

        // Act
        ProductResponse result = productService.getProductById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(productRepository).findById(productId);
    }

    @Test
    public void givenNonExistingProductId_whenGetProductById_thenThrowNotFoundException() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(productId);
        });

        // Verify
        verify(productRepository).findById(productId);
    }

    @Test
    public void givenValidRequest_whenCreateProduct_thenSaveAndReturnProduct() {
        // Arrange
        ProductRequest request = new ProductRequest();
        request.setSku("SKU-123");
        request.setCategoryId(2L);
        request.setMinStockLevel(5);

        Category category = new Category();
        category.setId(2L);
        category.setUser(mockUser);

        Product product = new Product();
        product.setSku("SKU-123");

        Product savedProduct = new Product();
        savedProduct.setId(10L);
        savedProduct.setSku("SKU-123");

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setId(10L);

        when(productRepository.findBySkuAndUserId("SKU-123", mockUser.getId())).thenReturn(Optional.empty());
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productMapper.toResponse(savedProduct)).thenReturn(mockResponse);
        when(stockRepository.findByProductIdAndUserId(10L, mockUser.getId())).thenReturn(new ArrayList<>());

        // Act
        ProductResponse result = productService.createProduct(request);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(productRepository).save(any(Product.class));
    }
}
