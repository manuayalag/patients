package com.fiuni.patients.repository;

import com.fiuni.clinica.domain.patient.PatientDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para PatientDomain usando entidades del JAR externo
 */
@Repository
public interface PatientRepository extends IBaseRepository<PatientDomain> {

    /**
     * Buscar paciente por número de documento (solo activos)
     */
    @Query("SELECT p FROM PatientDomain p WHERE p.documentNumber = :documentNumber AND p.isActive = true")
    Optional<PatientDomain> findByDocumentNumberAndActiveTrue(@Param("documentNumber") String documentNumber);

    /**
     * Buscar pacientes por término general con paginación
     */
    @Query("SELECT p FROM PatientDomain p WHERE p.isActive = true AND " +
           "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "p.documentNumber LIKE CONCAT('%', :searchTerm, '%'))")
    Page<PatientDomain> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}