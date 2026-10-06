package com.veterinaria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedienteRequest {

    @NotNull(message = "El id de la cita es obligatorio")
    private Long citaId;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    @NotBlank(message = "El tratamiento es obligatorio")
    private String tratamiento;

    @NotNull(message = "El peso en kilogramos es obligatorio")
    @Positive(message = "El peso debe ser mayor a 0")
    private Double pesoKg;
}
