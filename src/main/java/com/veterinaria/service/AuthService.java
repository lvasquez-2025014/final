package com.veterinaria.service;

import com.veterinaria.dto.AuthResponse;
import com.veterinaria.dto.LoginRequest;
import com.veterinaria.dto.RegisterRequest;
import com.veterinaria.entity.Rol;
import com.veterinaria.entity.Usuario;
import com.veterinaria.exception.ApiException;
import com.veterinaria.repository.UsuarioRepository;
import com.veterinaria.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("El correo electrónico ya está registrado: " + request.getEmail(), HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.CLIENTE) // Rol predeterminado CLIENTE según los requisitos
                .build();

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        String jwtToken = jwtService.generateToken(usuarioGuardado);

        return AuthResponse.builder()
                .token(jwtToken)
                .type("Bearer")
                .id(usuarioGuardado.getId())
                .nombre(usuarioGuardado.getNombre())
                .email(usuarioGuardado.getEmail())
                .rol(usuarioGuardado.getRol().name())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ApiException("Usuario no encontrado con el correo: " + request.getEmail(), HttpStatus.NOT_FOUND));

        String jwtToken = jwtService.generateToken(usuario);

        return AuthResponse.builder()
                .token(jwtToken)
                .type("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();
    }
}
