package com.veterinaria.dto;

import com.veterinaria.entity.Especie;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MascotaRequest {

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    private String nombre;

    @NotNull(message = "La especie es obligatoria (PERRO, GATO, AVE, OTRO)")
    private Especie especie;

    private String raza;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;

    // Opcional: si un ADMIN registra la mascota para un cliente específico
    private Long clienteId;

    @DecimalMin(value = "0.0", inclusive = false, message = "El peso debe ser mayor a 0")
    private BigDecimal pesoKg;
}
