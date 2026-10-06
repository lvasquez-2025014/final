package com.veterinaria.controller;

import com.veterinaria.dto.ExpedienteRequest;
import com.veterinaria.dto.ExpedienteResponse;
import com.veterinaria.service.IExpedienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expedientes")
@RequiredArgsConstructor
public class ExpedienteController {

    private final IExpedienteService expedienteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('VET', 'ADMIN')")
    public ResponseEntity<ExpedienteResponse> registrarExpediente(
            @Valid @RequestBody ExpedienteRequest request,
            Authentication authentication
    ) {
        ExpedienteResponse response = expedienteService.registrarExpediente(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/mascota/{mascotaId}")
    @PreAuthorize("hasAnyRole('VET', 'CLIENTE', 'ADMIN')")
    public ResponseEntity<List<ExpedienteResponse>> listarPorMascota(
            @PathVariable Long mascotaId,
            Authentication authentication
    ) {
        List<ExpedienteResponse> response = expedienteService.listarPorMascota(mascotaId, authentication.getName());
        return ResponseEntity.ok(response);
    }
}
