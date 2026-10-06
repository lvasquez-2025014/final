package com.veterinaria.repository;

import com.veterinaria.entity.ExpedienteClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpedienteClinicoRepository extends JpaRepository<ExpedienteClinico, Long> {

    boolean existsByCitaId(Long citaId);

    Optional<ExpedienteClinico> findByCitaId(Long citaId);

    List<ExpedienteClinico> findByCita_Mascota_IdOrderByFechaRegistroDesc(Long mascotaId);
}
