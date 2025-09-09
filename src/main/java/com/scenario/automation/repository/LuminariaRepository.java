package com.scenario.automation.repository;

import com.scenario.automation.model.Luminaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LuminariaRepository extends JpaRepository<Luminaria, Long> {

    /**
     * Buscar luminárias por ambiente ordenadas por nome
     */
    List<Luminaria> findByAmbienteIdOrderByNameAsc(Long ambienteId);

    /**
     * Verificar se existe luminária com nome específico no ambiente
     */
    boolean existsByNameIgnoreCaseAndAmbienteId(String name, Long ambienteId);

    /**
     * Verificar se existe luminária com nome específico no ambiente (excluindo ID específico)
     */
    boolean existsByNameIgnoreCaseAndAmbienteIdAndIdNot(String name, Long ambienteId, Long id);
}