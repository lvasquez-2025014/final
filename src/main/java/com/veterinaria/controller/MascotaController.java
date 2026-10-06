package com.veterinaria.controller;

import com.veterinaria.dto.MascotaRequest;
import com.veterinaria.dto.MascotaResponse;
import com.veterinaria.service.IMascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final IMascotaService mascotaService;

    @GetMapping("/mis-mascotas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<MascotaResponse>> obtenerMisMascotas(Authentication authentication) {
        List<MascotaResponse> mascotas = mascotaService.listarMisMascotas(authentication.getName());
        return ResponseEntity.ok(mascotas);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<MascotaResponse> registrarMascota(
            @Valid @RequestBody MascotaRequest request,
            Authentication authentication
    ) {
        MascotaResponse response = mascotaService.registrar(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VET', 'ADMIN')")
    public ResponseEntity<MascotaResponse> obtenerPorId(@PathVariable Long id) {
        MascotaResponse response = mascotaService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }
}
