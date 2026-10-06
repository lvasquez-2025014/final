package com.veterinaria.service;

import com.veterinaria.dto.MascotaRequest;
import com.veterinaria.dto.MascotaResponse;

import java.util.List;

public interface IMascotaService {

    List<MascotaResponse> listarMisMascotas(String emailCliente);

    MascotaResponse registrar(MascotaRequest request, String emailUsuarioAutenticado);

    MascotaResponse obtenerPorId(Long id);
}
