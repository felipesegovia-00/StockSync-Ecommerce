package com.stockSync.backend.user.controller;

import com.stockSync.backend.user.dto.CompanyUpdateRequest;
import com.stockSync.backend.user.model.Role;
import com.stockSync.backend.user.model.User;
import com.stockSync.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final UserRepository userRepository;

    @PutMapping("/logo")
    public ResponseEntity<?> updateCompanyLogo(@RequestBody CompanyUpdateRequest request) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Solo el ADMIN dueño de la cuenta puede cambiar el logo
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("message", "Solo el administrador puede actualizar el logo de la empresa"));
        }

        currentUser.setCompanyLogo(request.getCompanyLogo());
        userRepository.save(currentUser);

        return ResponseEntity.ok(Map.of("message", "Logo actualizado correctamente", "companyLogo", request.getCompanyLogo()));
    }
}
