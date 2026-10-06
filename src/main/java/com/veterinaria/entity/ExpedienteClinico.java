package com.veterinaria.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "expedientes_clinicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedienteClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", nullable = false, unique = true)
    private CitaMedica cita;

    @Column(nullable = false, length = 1000)
    private String diagnostico;

    @Column(nullable = false, length = 1000)
    private String tratamiento;

    @Column(name = "peso_kg", nullable = false)
    private Double pesoKg;

    @Column(name = "fecha_registro", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }
}
