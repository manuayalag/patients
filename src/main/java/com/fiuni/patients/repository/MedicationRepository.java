package com.fiuni.patients.repository;

import com.fiuni.clinica.domain.patient.MedicationDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para MedicationDomain usando entidades del JAR externo
 */
@Repository
public interface MedicationRepository extends IBaseRepository<MedicationDomain> {

    /**
     * Buscar medicamentos por término general con paginación
     */
    @Query("SELECT m FROM MedicationDomain m WHERE m.isActive = true AND " +
           "(:searchTerm IS NULL OR :searchTerm = '' OR " +
           "UPPER(m.medicationName) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(m.genericName) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(m.medicationType) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(m.manufacturer) LIKE UPPER(CONCAT('%', :searchTerm, '%')))")
    Page<MedicationDomain> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}