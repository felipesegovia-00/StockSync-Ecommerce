package com.stockSync.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "warehouse")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(nullable = false,length = 100)
    private String name;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(name = "date", nullable = false)
    private LocalDate createAt;

    @PrePersist
    public  void prePersist(){
        this.createAt = LocalDate.now();
    }

    // Constructor
    public Warehouse(Long id, String code, String name, String address, String city, LocalDate createAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.address = address;
        this.city = city;
        this.createAt = createAt;
    }

    // Constructor (empty)
    public Warehouse() {
    }

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDate createAt) {
        this.createAt = createAt;
    }
}
