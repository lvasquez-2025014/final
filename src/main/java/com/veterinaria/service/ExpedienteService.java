package com.veterinaria.service;

import com.veterinaria.dto.ExpedienteRequest;
import com.veterinaria.dto.ExpedienteResponse;
import com.veterinaria.entity.*;
import com.veterinaria.exception.ApiException;
import com.veterinaria.repository.CitaMedicaRepository;
import com.veterinaria.repository.ExpedienteClinicoRepository;
import com.veterinaria.repository.MascotaRepository;
import com.veterinaria.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpedienteService implements IExpedienteService {

    private final ExpedienteClinicoRepository expedienteClinicoRepository;
    private final CitaMedicaRepository citaMedicaRepository;
    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public ExpedienteResponse registrarExpediente(ExpedienteRequest request, String emailUsuarioAutenticado) {
        Usuario usuarioAutenticado = usuarioRepository.findByEmail(emailUsuarioAutenticado.toLowerCase().trim())
                .orElseThrow(() -> new ApiException("Usuario autenticado no encontrado", HttpStatus.NOT_FOUND));

        CitaMedica cita = citaMedicaRepository.findById(request.getCitaId())
                .orElseThrow(() -> new ApiException("Cita médica no encontrada con el id: " + request.getCitaId(), HttpStatus.NOT_FOUND));

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new ApiException("No se puede registrar un expediente clínico para una cita que ha sido CANCELADA", HttpStatus.BAD_REQUEST);
        }

        if (expedienteClinicoRepository.existsByCitaId(cita.getId())) {
            throw new ApiException("La cita médica con id " + cita.getId() + " ya tiene un expediente clínico registrado", HttpStatus.BAD_REQUEST);
        }

        // Si es VET, verificar que la cita esté asignada a este veterinario
        if (usuarioAutenticado.getRol() == Rol.VET && !cita.getVeterinario().getId().equals(usuarioAutenticado.getId())) {
            throw new ApiException("No tienes permiso para registrar expediente en una cita asignada a otro veterinario", HttpStatus.FORBIDDEN);
        }

        ExpedienteClinico expediente = ExpedienteClinico.builder()
                .cita(cita)
                .diagnostico(request.getDiagnostico().trim())
                .tratamiento(request.getTratamiento().trim())
                .pesoKg(request.getPesoKg())
                .build();

        ExpedienteClinico expedienteGuardado = expedienteClinicoRepository.save(expediente);

        // Actualizar el estado de la cita a COMPLETADA
        cita.setEstado(EstadoCita.COMPLETADA);
        citaMedicaRepository.save(cita);

        return mapToResponse(expedienteGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpedienteResponse> listarPorMascota(Long mascotaId, String emailUsuarioAutenticado) {
        Usuario usuarioAutenticado = usuarioRepository.findByEmail(emailUsuarioAutenticado.toLowerCase().trim())
                .orElseThrow(() -> new ApiException("Usuario autenticado no encontrado", HttpStatus.NOT_FOUND));

        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new ApiException("Mascota no encontrada con el id: " + mascotaId, HttpStatus.NOT_FOUND));

        // Si es CLIENTE, validar que la mascota le pertenezca
        if (usuarioAutenticado.getRol() == Rol.CLIENTE && !mascota.getCliente().getId().equals(usuarioAutenticado.getId())) {
            throw new ApiException("No tienes permiso para consultar los expedientes de una mascota que no te pertenece", HttpStatus.FORBIDDEN);
        }

        return expedienteClinicoRepository.findByCita_Mascota_IdOrderByFechaRegistroDesc(mascotaId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ExpedienteResponse mapToResponse(ExpedienteClinico exp) {
        CitaMedica cita = exp.getCita();
        Mascota mascota = cita.getMascota();
        Usuario cliente = mascota.getCliente();
        Usuario vet = cita.getVeterinario();

        return ExpedienteResponse.builder()
                .id(exp.getId())
                .citaId(cita.getId())
                .mascotaId(mascota.getId())
                .mascotaNombre(mascota.getNombre())
                .mascotaEspecie(mascota.getEspecie().name())
                .clienteId(cliente.getId())
                .clienteNombre(cliente.getNombre())
                .veterinarioId(vet.getId())
                .veterinarioNombre(vet.getNombre())
                .diagnostico(exp.getDiagnostico())
                .tratamiento(exp.getTratamiento())
                .pesoKg(exp.getPesoKg())
                .fechaRegistro(exp.getFechaRegistro())
                .build();
    }
}
