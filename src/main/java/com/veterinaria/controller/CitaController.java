package com.veterinaria.controller;

import com.veterinaria.dto.CitaRequest;
import com.veterinaria.dto.CitaResponse;
import com.veterinaria.service.ICitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/citas")
@RequiredArgsConstructor
public class CitaController {

    private final ICitaService citaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<CitaResponse> agendarCita(
            @Valid @RequestBody CitaRequest request,
            Authentication authentication
    ) {
        CitaResponse response = citaService.agendarCita(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/agenda")
    @PreAuthorize("hasAnyRole('VET', 'ADMIN')")
    public ResponseEntity<List<CitaResponse>> listarAgenda(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Long veterinarioId
    ) {
        List<CitaResponse> agenda = citaService.listarAgenda(fecha, veterinarioId);
        return ResponseEntity.ok(agenda);
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<CitaResponse> cancelarCita(
            @PathVariable Long id,
            Authentication authentication
    ) {
        CitaResponse response = citaService.cancelarCita(id, authentication.getName());
        return ResponseEntity.ok(response);
    }
}
