package com.veterinaria.service;

import com.veterinaria.dto.CitaRequest;
import com.veterinaria.dto.CitaResponse;

import java.time.LocalDate;
import java.util.List;

public interface ICitaService {

    CitaResponse agendarCita(CitaRequest request, String emailUsuarioAutenticado);

    List<CitaResponse> listarAgenda(LocalDate fecha, Long veterinarioId);

    CitaResponse cancelarCita(Long id, String emailUsuarioAutenticado);
}
