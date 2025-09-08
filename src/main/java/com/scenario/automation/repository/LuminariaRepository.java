package com.scenario.automation.repository;

import com.scenario.automation.model.Luminaria;
import com.scenario.automation.model.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LuminariaRepository extends JpaRepository<Luminaria, Long> {

    /**
     * Buscar luminárias por ambiente
     */
    List<Luminaria> findByAmbienteId(Long ambienteId);

    /**
     * Buscar luminárias por ambiente ordenadas por nome
     */
    List<Luminaria> findByAmbienteIdOrderByNameAsc(Long ambienteId);

    /**
     * Buscar luminárias por tipo
     */
    List<Luminaria> findByTypeIgnoreCase(String type);

    /**
     * Buscar luminárias ligadas
     */
    List<Luminaria> findByStatusTrue();

    /**
     * Buscar luminárias desligadas
     */
    List<Luminaria> findByStatusFalse();

    /**
     * Buscar luminárias ligadas por ambiente
     */
    List<Luminaria> findByAmbienteIdAndStatusTrue(Long ambienteId);

    /**
     * Buscar luminárias por nome (case insensitive)
     */
    List<Luminaria> findByNameContainingIgnoreCase(String name);

    /**
     * Buscar luminária por nome e ambiente
     */
    Optional<Luminaria> findByNameIgnoreCaseAndAmbienteId(String name, Long ambienteId);

    /**
     * Contar luminárias por ambiente
     */
    long countByAmbienteId(Long ambienteId);

    /**
     * Contar luminárias ligadas por ambiente
     */
    long countByAmbienteIdAndStatusTrue(Long ambienteId);

    /**
     * Verificar se existe luminária com nome no ambiente (excluindo o próprio ID)
     */
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Luminaria l WHERE LOWER(l.name) = LOWER(:name) AND l.ambiente.id = :ambienteId AND l.id <> :id")
    boolean existsByNameIgnoreCaseAndAmbienteIdAndIdNot(@Param("name") String name, @Param("ambienteId") Long ambienteId, @Param("id") Long id);

    /**
     * Verificar se existe luminária com nome no ambiente
     */
    boolean existsByNameIgnoreCaseAndAmbienteId(String name, Long ambienteId);

    /**
     * Buscar luminárias por faixa de brilho
     */
    @Query("SELECT l FROM Luminaria l WHERE l.brightness BETWEEN :minBrightness AND :maxBrightness")
    List<Luminaria> findByBrightnessRange(@Param("minBrightness") Integer minBrightness, @Param("maxBrightness") Integer maxBrightness);

    /**
     * Deletar todas as luminárias de um ambiente
     */
    void deleteByAmbienteId(Long ambienteId);
}
