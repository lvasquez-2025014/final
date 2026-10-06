package com.veterinaria;

import com.veterinaria.entity.Especie;
import com.veterinaria.entity.Mascota;
import com.veterinaria.entity.Rol;
import com.veterinaria.entity.Usuario;
import com.veterinaria.repository.MascotaRepository;
import com.veterinaria.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final MascotaRepository mascotaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            log.info("Inicializando usuarios y datos por defecto...");

            // 1. ADMIN (contraseña: Admin123*)
            Usuario admin = Usuario.builder()
                    .nombre("Administrador Principal")
                    .email("admin@veterinaria.com")
                    .telefono("50212345678")
                    .password(passwordEncoder.encode("Admin123*"))
                    .rol(Rol.ADMIN)
                    .build();
            usuarioRepository.save(admin);

            // 2. VET (contraseña: Vet123*)
            Usuario vet = Usuario.builder()
                    .nombre("Dr. Carlos Martinez (Veterinario)")
                    .email("vet@veterinaria.com")
                    .telefono("50287654321")
                    .password(passwordEncoder.encode("Vet123*"))
                    .rol(Rol.VET)
                    .build();
            usuarioRepository.save(vet);

            // 3. CLIENTE (contraseña: Cliente123*)
            Usuario cliente = Usuario.builder()
                    .nombre("Juan Perez (Cliente)")
                    .email("cliente@veterinaria.com")
                    .telefono("50255554444")
                    .password(passwordEncoder.encode("Cliente123*"))
                    .rol(Rol.CLIENTE)
                    .build();
            usuarioRepository.save(cliente);

            // Mascota de ejemplo
            Mascota mascota = Mascota.builder()
                    .nombre("Firulais")
                    .especie(Especie.PERRO)
                    .raza("Golden Retriever")
                    .edad(3)
                    .cliente(cliente)
                    .build();
            mascotaRepository.save(mascota);

            log.info("Carga inicial completada: 1 ADMIN, 1 VET, 1 CLIENTE y 1 Mascota de ejemplo.");
        }
    }
}
