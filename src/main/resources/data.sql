-- Carga inicial de usuarios requerida por la evaluación técnica
-- Contraseña para todos los usuarios de prueba: Admin123* (cifrada con BCrypt)

INSERT IGNORE INTO usuarios (id, email, nombre, password, rol, telefono)
VALUES 
(1, 'admin@veterinaria.com', 'Administrador Principal', '$2a$10$7C1fNf0sT0ydkBmKBgWaP.syFw8KWse9oCdwPvothb7DG9EgILPAK', 'ADMIN', '50212345678'),
(2, 'vet@veterinaria.com', 'Dr. Carlos Martinez (Veterinario)', '$2a$10$7C1fNf0sT0ydkBmKBgWaP.syFw8KWse9oCdwPvothb7DG9EgILPAK', 'VET', '50287654321'),
(3, 'cliente@veterinaria.com', 'Juan Perez (Cliente)', '$2a$10$7C1fNf0sT0ydkBmKBgWaP.syFw8KWse9oCdwPvothb7DG9EgILPAK', 'CLIENTE', '50255554444');

INSERT IGNORE INTO mascotas (id, nombre, especie, raza, edad, cliente_id)
VALUES 
(1, 'Firulais', 'PERRO', 'Golden Retriever', 3, 3);
