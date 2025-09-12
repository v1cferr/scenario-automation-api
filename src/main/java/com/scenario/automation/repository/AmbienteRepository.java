package com.scenario.automation.repository;

import com.scenario.automation.model.Ambiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {

    /**
     * Buscar ambiente por nome (case insensitive)
     */
    Optional<Ambiente> findByNameIgnoreCase(String name);

    /**
     * Buscar ambientes que contenham o texto no nome ou descrição
     */
    @Query("SELECT a FROM Ambiente a WHERE " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Ambiente> findByNameOrDescriptionContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Verificar se existe ambiente com o nome (excluindo o próprio ID)
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Ambiente a WHERE LOWER(a.name) = LOWER(:name) AND a.id <> :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);

    /**
     * Verificar se existe ambiente com o nome
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Buscar ambientes ordenados por nome
     */
    List<Ambiente> findAllByOrderByNameAsc();

    /**
     * Buscar ambientes com luminárias (usando fetch join para evitar N+1)
     */
    @Query("SELECT DISTINCT a FROM Ambiente a LEFT JOIN FETCH a.luminarias")
    List<Ambiente> findAllWithLuminarias();

    /**
     * Buscar ambientes por subambiente
     */
    List<Ambiente> findBySubambienteIgnoreCase(String subambiente);

    /**
     * Buscar todos os subambientes únicos (não nulos)
     */
    @Query("SELECT DISTINCT a.subambiente FROM Ambiente a WHERE a.subambiente IS NOT NULL ORDER BY a.subambiente")
    List<String> findAllDistinctSubambientes();

    /**
     * Verificar se existe ambiente com o subambiente
     */
    boolean existsBySubambienteIgnoreCase(String subambiente);
}
