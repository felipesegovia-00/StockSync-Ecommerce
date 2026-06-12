package com.stockSync.backend.auth.service;


import com.stockSync.backend.auth.dto.*;
import com.stockSync.backend.user.model.Role;
import com.stockSync.backend.security.service.JwtService;
import com.stockSync.backend.user.model.User;
import com.stockSync.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.stockSync.backend.common.exception.ConflictException;
import com.stockSync.backend.common.exception.BadRequestException;
import com.stockSync.backend.common.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final com.stockSync.backend.stock.repository.WarehouseRepository warehouseRepository;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("El email ya existe");
        }

        var user = User.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(Objects.requireNonNull(passwordEncoder.encode(request.getPassword())))
                .role(Role.ADMIN)
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        var jwtToken = jwtService.generateToken(user, Boolean.TRUE.equals(request.getRememberMe()));

        AuthResponse.WarehouseDto warehouseDto = null;
        if (user.getAssignedWarehouse() != null) {
            warehouseDto = AuthResponse.WarehouseDto.builder()
                    .id(user.getAssignedWarehouse().getId())
                    .name(user.getAssignedWarehouse().getName())
                    .build();
        }

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .nombre(user.getNombre())
                .role(user.getRole().name())
                .forcePasswordChange(user.isForcePasswordChange())
                .assignedWarehouse(warehouseDto)
                .build();
    }

    public InviteResponse invite(InviteRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("El email ya existe");
        }

        User admin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        
        com.stockSync.backend.stock.model.Warehouse warehouse = null;
        if (request.getAssignedWarehouseId() != null) {
            warehouse = warehouseRepository.findById(request.getAssignedWarehouseId()).orElse(null);
        }

        var user = User.builder()
                .nombre(request.getEmail().split("@")[0])
                .email(request.getEmail())
                .password(Objects.requireNonNull(passwordEncoder.encode(tempPassword)))
                .role(request.getRole())
                .owner(admin)
                .forcePasswordChange(true)
                .assignedWarehouse(warehouse)
                .active(true)
                .build();

        userRepository.save(user);

        return InviteResponse.builder()
                .email(user.getEmail())
                .role(user.getRole().name())
                .temporaryPassword(tempPassword)
                .build();
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            throw new BadCredentialsException("La contraseña actual no es correcta");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        currentUser.setForcePasswordChange(false);
        userRepository.save(currentUser);
    }

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1)); // El token es válido por 1 hora
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return "Email de recuperación enviado"; 
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        var user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Token inválido o expirado"));

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("El token ha expirado");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        user.setForcePasswordChange(false); // Por si tenía el flag activo
        userRepository.save(user);
    }
}