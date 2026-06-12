package com.stockSync.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import com.stockSync.backend.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"owner", "assignedWarehouse"})
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByCompanyName(String companyName);

    List<User> findByOwnerId(Long ownerId);

    Optional<User> findByResetPasswordToken(String token);
}