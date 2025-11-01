package com.fiuni.patients.repository;

import com.fiuni.clinica.domain.patient.PrescriptionDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository para PrescriptionDomain usando entidades del JAR externo
 */
@Repository
public interface PrescriptionRepository extends IBaseRepository<PrescriptionDomain> {

    /**
     * Buscar prescripciones por paciente con paginación
     */
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.patient.id = :patientId AND p.isActive = true")
    Page<PrescriptionDomain> findByPatientIdAndIsActiveTrue(@Param("patientId") Integer patientId, Pageable pageable);

    /**
     * Buscar prescripciones por término general con paginación
     */
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.isActive = true AND " +
        "(:searchTerm IS NULL OR :searchTerm = '' OR " +
        "UPPER(p.doctorName) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
        "UPPER(p.doctorLicense) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
        "UPPER(p.notes) LIKE UPPER(CONCAT('%', :searchTerm, '%')))")
    Page<PrescriptionDomain> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}