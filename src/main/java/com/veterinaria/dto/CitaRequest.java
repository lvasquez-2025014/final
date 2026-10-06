package com.veterinaria.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaRequest {

    @NotNull(message = "El id de la mascota es obligatorio")
    private Long mascotaId;

    @NotNull(message = "El id del veterinario es obligatorio")
    private Long veterinarioId;

    @NotNull(message = "La fecha y hora de la cita es obligatoria")
    @Future(message = "La fecha y hora de la cita debe ser una fecha futura")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo de la cita es obligatorio")
    private String motivo;
}
