package com.veterinaria.service;

import com.veterinaria.dto.ExpedienteRequest;
import com.veterinaria.dto.ExpedienteResponse;

import java.util.List;

public interface IExpedienteService {

    ExpedienteResponse registrarExpediente(ExpedienteRequest request, String emailUsuarioAutenticado);

    List<ExpedienteResponse> listarPorMascota(Long mascotaId, String emailUsuarioAutenticado);
}
