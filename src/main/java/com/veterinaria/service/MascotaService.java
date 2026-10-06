package com.veterinaria.service;

import com.veterinaria.dto.MascotaRequest;
import com.veterinaria.dto.MascotaResponse;
import com.veterinaria.entity.Mascota;
import com.veterinaria.entity.Rol;
import com.veterinaria.entity.Usuario;
import com.veterinaria.exception.ApiException;
import com.veterinaria.repository.MascotaRepository;
import com.veterinaria.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MascotaService implements IMascotaService {

    private final MascotaRepository mascotaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponse> listarMisMascotas(String emailCliente) {
        return mascotaRepository.findByClienteEmail(emailCliente.toLowerCase().trim())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public MascotaResponse registrar(MascotaRequest request, String emailUsuarioAutenticado) {
        Usuario usuarioAutenticado = usuarioRepository.findByEmail(emailUsuarioAutenticado.toLowerCase().trim())
                .orElseThrow(() -> new ApiException("Usuario autenticado no encontrado", HttpStatus.NOT_FOUND));

        Usuario duenoMascota;

        // Si es ADMIN y proporciona clienteId, se asocia al cliente especificado
        if (usuarioAutenticado.getRol() == Rol.ADMIN && request.getClienteId() != null) {
            duenoMascota = usuarioRepository.findById(request.getClienteId())
                    .orElseThrow(() -> new ApiException("Cliente no encontrado con id: " + request.getClienteId(), HttpStatus.NOT_FOUND));
        } else {
            // Si es CLIENTE (o ADMIN sin clienteId específico), el dueño es el usuario autenticado
            duenoMascota = usuarioAutenticado;
        }

        Mascota mascota = Mascota.builder()
                .nombre(request.getNombre().trim())
                .especie(request.getEspecie())
                .raza(request.getRaza() != null ? request.getRaza().trim() : null)
                .edad(request.getEdad())
                .cliente(duenoMascota)
                .build();

        Mascota mascotaGuardada = mascotaRepository.save(mascota);
        return mapToResponse(mascotaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public MascotaResponse obtenerPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ApiException("Ficha de mascota no encontrada con el id: " + id, HttpStatus.NOT_FOUND));
        return mapToResponse(mascota);
    }

    private MascotaResponse mapToResponse(Mascota mascota) {
        return MascotaResponse.builder()
                .id(mascota.getId())
                .nombre(mascota.getNombre())
                .especie(mascota.getEspecie())
                .raza(mascota.getRaza())
                .edad(mascota.getEdad())
                .clienteId(mascota.getCliente().getId())
                .clienteNombre(mascota.getCliente().getNombre())
                .clienteEmail(mascota.getCliente().getEmail())
                .clienteTelefono(mascota.getCliente().getTelefono())
                .build();
    }
}
