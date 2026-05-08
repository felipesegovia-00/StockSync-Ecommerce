package com.stockSync.backend.stock.model;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDate;

@Data
@Entity
@Table(name = "warehouses")
public class Warehouse {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(name = "date", nullable = false)
    private LocalDate createAt;

    @PrePersist
    public void prePersist() {
        this.createAt = LocalDate.now();
    }

}