# API REST - Sistema de Control de Citas para Veterinaria

API REST backend completa desarrollada con **Spring Boot 3.4.3**, **Java 21**, **Maven**, **Spring Security (JWT)** y **MySQL** para la gestión integral de pacientes (mascotas), agenda médica y expedientes clínicos.

---

## 🛠️ Tecnologías y Arquitectura

- **Lenguaje:** Java 21 LTS
- **Framework:** Spring Boot 3.4.3
- **Seguridad:** Spring Security con autenticación Stateless mediante JWT (`io.jsonwebtoken:jjwt`)
- **Persistencia:** Spring Data JPA / Hibernate ORM
- **Base de Datos:** MySQL (`dbveterinaria_final_in5am`)
- **Mapeo y Boilerplate:** Lombok 1.18.36
- **Validación:** Jakarta Bean Validation
- **Arquitectura de Capas (KinalApp):**
  - `entity`: Entidades JPA y Enums del modelo relacional.
  - `repository`: Interfaces Spring Data JPA con consultas JPQL especializadas.
  - `service`: Interfaces de servicio (`I...Service`) e implementaciones de lógica de negocio.
  - `controller`: Controladores REST con `@RestController` y control de acceso basado en roles con `@PreAuthorize`.
  - `dto`: Clases Request/Response desacopladas para evitar recursión de Jackson.
  - `security`: Filtro JWT (`JwtAuthenticationFilter`), proveedor (`JwtService`) y configuración de seguridad (`SecurityConfig`).
  - `exception`: Manejo centralizado de excepciones con `@RestControllerAdvice` y respuesta estandarizada (`ErrorResponse`).

---

## 👥 Credenciales de Prueba Precargadas

El sistema cuenta con carga inicial automática mediante `DataLoader.java` y `data.sql`:

| Rol | Correo Electrónico | Contraseña | Propósito |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@veterinaria.com` | `Admin123*` | Control total del sistema |
| **VET** | `vet@veterinaria.com` | `Vet123*` | Gestión de agenda y registro de expedientes |
| **CLIENTE** | `cliente@veterinaria.com` | `Cliente123*` | Registro de mascotas y solicitud de citas |

*Nota: Todas las contraseñas están encriptadas con **BCrypt**.*

---

## 📌 Matriz de Endpoints

### 1. Autenticación y Registro (`/api/v1/auth`)
| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Público | Registro de usuarios nuevos (rol asignado por defecto: `CLIENTE`). |
| `POST` | `/api/v1/auth/login` | Público | Autenticación con email/password. Retorna el token JWT y datos de usuario. |

### 2. Gestión de Mascotas (`/api/v1/mascotas`)
| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/mascotas/mis-mascotas` | `CLIENTE` | Retorna las mascotas del cliente en sesión extraído del token JWT. |
| `POST` | `/api/v1/mascotas` | `CLIENTE`, `ADMIN` | Registrar una nueva mascota (asociada al cliente actual o especificado si es ADMIN). |
| `GET` | `/api/v1/mascotas/{id}` | `VET`, `ADMIN` | Consulta de ficha técnica completa de una mascota. |

### 3. Gestión de Citas Médicas (`/api/v1/citas`)
| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/citas` | `CLIENTE`, `ADMIN` | Agendar cita médica. Valida disponibilidad del veterinario (30 min) y límite de 2 citas pendientes por cliente/día. |
| `GET` | `/api/v1/citas/agenda` | `VET`, `ADMIN` | Consulta agenda de citas con filtros opcionales por fecha (`yyyy-MM-dd`) y veterinario (`veterinarioId`). |
| `PATCH` | `/api/v1/citas/{id}/cancelar` | `CLIENTE`, `ADMIN` | Cancelación de cita programada. Valida la regla de anticipación de más de 2 horas. |

### 4. Expedientes Clínicos (`/api/v1/expedientes`)
| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/expedientes` | `VET`, `ADMIN` | Registrar diagnóstico, tratamiento y peso de la cita. Actualiza automáticamente la cita a estado `COMPLETADA`. |
| `GET` | `/api/v1/expedientes/mascota/{mascotaId}` | `VET`, `CLIENTE`, `ADMIN` | Consulta el historial clínico completo de diagnósticos y tratamientos de una mascota. |

---

## ⚙️ Reglas de Negocio Implementadas

1. **Disponibilidad del Veterinario:** Cada cita médica tiene una duración estimada fija de 30 minutos. El sistema bloquea automáticamente solicitudes que coincidan o se traslapen con citas activas del mismo veterinario.
2. **Límite Diario por Cliente:** Un cliente no puede tener más de 2 citas en estado `PENDIENTE` para el mismo día calendario.
3. **Cancelación con Anticipación:** Solo se permite la cancelación si faltan más de 2 horas para la hora programada de la cita. Citas completadas o previamente canceladas no pueden modificarse.
4. **Propiedad y Privacidad:** Un cliente únicamente puede consultar/agendar citas y expedientes correspondientes a sus propias mascotas.
5. **Cierre Automático de Citas:** El registro de un expediente clínico para una cita formaliza la atención y transiciona el estado de la cita médica a `COMPLETADA`.

---

## 🚀 Ejecución del Proyecto

### Requisitos
- JDK 21
- MySQL Server corriendo en puerto 3306

### Comandos
```bash
# Compilar y empaquetar
./mvnw clean package -DskipTests

# Ejecutar pruebas unitarias e integración
./mvnw test

# Iniciar servidor Spring Boot
./mvnw spring-boot:run
```
La API estará disponible en `http://localhost:8081`.
