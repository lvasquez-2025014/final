package com.veterinaria.service;

import com.veterinaria.dto.CitaRequest;
import com.veterinaria.dto.CitaResponse;
import com.veterinaria.entity.*;
import com.veterinaria.exception.ApiException;
import com.veterinaria.repository.CitaMedicaRepository;
import com.veterinaria.repository.MascotaRepository;
import com.veterinaria.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService implements ICitaService {

    private final CitaMedicaRepository citaMedicaRepository;
    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public CitaResponse agendarCita(CitaRequest request, String emailUsuarioAutenticado) {
        Usuario usuarioAutenticado = usuarioRepository.findByEmail(emailUsuarioAutenticado.toLowerCase().trim())
                .orElseThrow(() -> new ApiException("Usuario autenticado no encontrado", HttpStatus.NOT_FOUND));

        Mascota mascota = mascotaRepository.findById(request.getMascotaId())
                .orElseThrow(() -> new ApiException("Mascota no encontrada con el id: " + request.getMascotaId(), HttpStatus.NOT_FOUND));

        // Si el usuario es CLIENTE, validar que la mascota le pertenezca
        if (usuarioAutenticado.getRol() == Rol.CLIENTE && !mascota.getCliente().getId().equals(usuarioAutenticado.getId())) {
            throw new ApiException("No tienes permiso para agendar citas para una mascota que no te pertenece", HttpStatus.FORBIDDEN);
        }

        Usuario veterinario = usuarioRepository.findById(request.getVeterinarioId())
                .orElseThrow(() -> new ApiException("Veterinario no encontrado con el id: " + request.getVeterinarioId(), HttpStatus.NOT_FOUND));

        if (veterinario.getRol() != Rol.VET) {
            throw new ApiException("El usuario asignado no tiene el rol de VETERINARIO (VET)", HttpStatus.BAD_REQUEST);
        }

        // Regla de Negocio 2: Límite de 2 citas PENDIENTES por día para el cliente
        LocalDate fechaCita = request.getFechaHora().toLocalDate();
        LocalDateTime inicioDia = fechaCita.atStartOfDay();
        LocalDateTime finDia = fechaCita.atTime(LocalTime.MAX);

        long citasPendientesCliente = citaMedicaRepository.countCitasClienteEnFecha(
                mascota.getCliente().getId(),
                EstadoCita.PENDIENTE,
                inicioDia,
                finDia
        );

        if (citasPendientesCliente >= 2) {
            throw new ApiException("El cliente ya tiene 2 citas en estado PENDIENTE para la fecha " + fechaCita + ". No se permite programar más de 2 citas pendientes el mismo día.", HttpStatus.BAD_REQUEST);
        }

        // Regla de Negocio 1: Disponibilidad del Veterinario (duración fija de 30 minutos)
        // Se valida traslape en el rango de 30 minutos antes y después
        LocalDateTime rangoInicio = request.getFechaHora().minusMinutes(30);
        LocalDateTime rangoFin = request.getFechaHora().plusMinutes(30);

        long solapamientos = citaMedicaRepository.countVeterinarioSolapamiento(
                veterinario.getId(),
                rangoInicio,
                rangoFin,
                EstadoCita.CANCELADA
        );

        if (solapamientos > 0) {
            throw new ApiException("El veterinario ya tiene una cita programada en ese horario. Las citas tienen una duración fija de 30 minutos.", HttpStatus.BAD_REQUEST);
        }

        CitaMedica cita = CitaMedica.builder()
                .mascota(mascota)
                .veterinario(veterinario)
                .fechaHora(request.getFechaHora())
                .motivo(request.getMotivo().trim())
                .estado(EstadoCita.PENDIENTE)
                .build();

        CitaMedica citaGuardada = citaMedicaRepository.save(cita);
        return mapToResponse(citaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listarAgenda(LocalDate fecha, Long veterinarioId) {
        LocalDateTime inicioDia = fecha != null ? fecha.atStartOfDay() : null;
        LocalDateTime finDia = fecha != null ? fecha.atTime(LocalTime.MAX) : null;

        return citaMedicaRepository.buscarAgenda(veterinarioId, inicioDia, finDia)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public CitaResponse cancelarCita(Long id, String emailUsuarioAutenticado) {
        CitaMedica cita = citaMedicaRepository.findById(id)
                .orElseThrow(() -> new ApiException("Cita médica no encontrada con el id: " + id, HttpStatus.NOT_FOUND));

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new ApiException("La cita ya se encuentra en estado CANCELADA", HttpStatus.BAD_REQUEST);
        }

        if (cita.getEstado() == EstadoCita.COMPLETADA) {
            throw new ApiException("No se puede cancelar una cita que ya fue COMPLETADA", HttpStatus.BAD_REQUEST);
        }

        Usuario usuarioAutenticado = usuarioRepository.findByEmail(emailUsuarioAutenticado.toLowerCase().trim())
                .orElseThrow(() -> new ApiException("Usuario autenticado no encontrado", HttpStatus.NOT_FOUND));

        // Si es CLIENTE, verificar que la cita le pertenezca
        if (usuarioAutenticado.getRol() == Rol.CLIENTE && !cita.getMascota().getCliente().getId().equals(usuarioAutenticado.getId())) {
            throw new ApiException("No tienes permiso para cancelar citas de mascotas que no te pertenecen", HttpStatus.FORBIDDEN);
        }

        // Regla de Negocio 3: Cancelación con anticipación (más de 2 horas antes de la hora programada)
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime limiteCancelacion = cita.getFechaHora().minusHours(2);

        if (!ahora.isBefore(limiteCancelacion)) {
            throw new ApiException("Una cita solo puede ser cancelada si faltan más de 2 horas para la hora programada.", HttpStatus.BAD_REQUEST);
        }

        cita.setEstado(EstadoCita.CANCELADA);
        CitaMedica citaActualizada = citaMedicaRepository.save(cita);
        return mapToResponse(citaActualizada);
    }

    private CitaResponse mapToResponse(CitaMedica cita) {
        return CitaResponse.builder()
                .id(cita.getId())
                .mascotaId(cita.getMascota().getId())
                .mascotaNombre(cita.getMascota().getNombre())
                .mascotaEspecie(cita.getMascota().getEspecie().name())
                .clienteId(cita.getMascota().getCliente().getId())
                .clienteNombre(cita.getMascota().getCliente().getNombre())
                .veterinarioId(cita.getVeterinario().getId())
                .veterinarioNombre(cita.getVeterinario().getNombre())
                .fechaHora(cita.getFechaHora())
                .motivo(cita.getMotivo())
                .estado(cita.getEstado())
                .build();
    }
}
