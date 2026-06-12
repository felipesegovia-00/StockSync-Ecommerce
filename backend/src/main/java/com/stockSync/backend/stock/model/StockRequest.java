package com.stockSync.backend.stock.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_warehouse_id", nullable = false)
    private Warehouse sourceWarehouse; // Bodega que provee (la principal)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_warehouse_id", nullable = false)
    private Warehouse destinationWarehouse; // Local que pide y recepciona

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 20)
    private String status; // PENDING, SHIPPED, RECEIVED, REJECTED

    @Column(name = "tracking_sku", length = 50, unique = true)
    private String trackingSku;

    @Column(name = "request_group_code", length = 50)
    private String requestGroupCode;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
