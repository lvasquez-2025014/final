package com.veterinaria.repository;

import com.veterinaria.entity.CitaMedica;
import com.veterinaria.entity.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Long> {

    @Query("SELECT COUNT(c) FROM CitaMedica c WHERE c.veterinario.id = :veterinarioId " +
           "AND c.estado != :cancelada " +
           "AND c.fechaHora > :rangoInicio AND c.fechaHora < :rangoFin")
    long countVeterinarioSolapamiento(
            @Param("veterinarioId") Long veterinarioId,
            @Param("rangoInicio") LocalDateTime rangoInicio,
            @Param("rangoFin") LocalDateTime rangoFin,
            @Param("cancelada") EstadoCita cancelada
    );

    @Query("SELECT COUNT(c) FROM CitaMedica c WHERE c.mascota.cliente.id = :clienteId " +
           "AND c.estado = :estado " +
           "AND c.fechaHora >= :inicioDia AND c.fechaHora <= :finDia")
    long countCitasClienteEnFecha(
            @Param("clienteId") Long clienteId,
            @Param("estado") EstadoCita estado,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia
    );

    @Query("SELECT c FROM CitaMedica c WHERE " +
           "(:veterinarioId IS NULL OR c.veterinario.id = :veterinarioId) AND " +
           "(:inicioDia IS NULL OR c.fechaHora >= :inicioDia) AND " +
           "(:finDia IS NULL OR c.fechaHora <= :finDia) " +
           "ORDER BY c.fechaHora ASC")
    List<CitaMedica> buscarAgenda(
            @Param("veterinarioId") Long veterinarioId,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia
    );
}
