package com.fiuni.patients.repository;

import com.fiuni.clinica.domain.patient.PatientDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para PatientDomain usando entidades del JAR externo
 */
@Repository
public interface PatientRepository extends JpaRepository<PatientDomain, Integer> {

    /**
     * Buscar pacientes activos usando isActive
     */
    @Query("SELECT p FROM PatientDomain p WHERE p.isActive = true")
    Page<PatientDomain> findAllActive(Pageable pageable);

    /**
     * Buscar paciente por ID y activo usando isActive
     */
    @Query("SELECT p FROM PatientDomain p WHERE p.id = :id AND p.isActive = true")
    Optional<PatientDomain> findByIdAndActiveTrue(@Param("id") Integer id);

    /**
     * Búsqueda simplificada de pacientes por ID (sin usar campos específicos hasta conocer la estructura)
     */
    @Query("SELECT p FROM PatientDomain p WHERE p.isActive = true")
    List<PatientDomain> searchPatients(@Param("searchTerm") String searchTerm);

    /**
     * Contar pacientes activos
     */
    @Query("SELECT COUNT(p) FROM PatientDomain p WHERE p.isActive = true")
    long countActivePatients();

    /**
     * Buscar pacientes por término general con paginación (búsqueda básica por ID)
     */
    @Query("SELECT p FROM PatientDomain p WHERE p.isActive = true")
    Page<PatientDomain> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}