package com.example.patients.repository;

import com.example.patients.repository.base.IBaseRepository;
import com.fiuni.clinica.domain.patient.PrescriptionDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Prescription entities
 * Manages prescription data with soft delete support
 */
@Repository
public interface PrescriptionRepository extends IBaseRepository<PrescriptionDomain> {
    
    // Métodos específicos para PrescriptionDomain (que usa active como Boolean)
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.id = :id AND p.active = true")
    Optional<PrescriptionDomain> findByIdAndActiveTrue(@Param("id") Integer id);
    
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.active = true")
    Page<PrescriptionDomain> findByActiveTrue(Pageable pageable);
    
    /**
     * Find prescriptions by patient ID (only active)
     */
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.patient.id = :patientId AND p.active = true")
    Page<PrescriptionDomain> findByPatientIdAndActiveTrue(@Param("patientId") Integer patientId, Pageable pageable);
    
    /**
     * Find prescriptions by patient ID (only active) - List version
     */
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.patient.id = :patientId AND p.active = true")
    List<PrescriptionDomain> findByPatientIdAndActiveTrue(@Param("patientId") Integer patientId);
    
    /**
     * Find prescriptions that have medications (only active)
     */
    @Query("SELECT DISTINCT p FROM PrescriptionDomain p JOIN p.medications m WHERE p.active = true")
    Page<PrescriptionDomain> findPrescriptionsWithMedicationsAndActiveTrue(Pageable pageable);
    
    /**
     * Find prescriptions by medication ID (only active)
     */
    @Query("SELECT DISTINCT p FROM PrescriptionDomain p JOIN p.medications m WHERE m.id = :medicationId AND p.active = true")
    List<PrescriptionDomain> findByMedicationIdAndActiveTrue(@Param("medicationId") Integer medicationId);
    
    /**
     * Count prescriptions by patient (only active)
     */
    @Query("SELECT COUNT(p) FROM PrescriptionDomain p WHERE p.patient.id = :patientId AND p.active = true")
    long countByPatientIdAndActiveTrue(@Param("patientId") Integer patientId);
    
    /**
     * Find prescriptions by patient ID with isActive naming (backward compatibility)
     */
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.patient.id = :patientId AND p.active = true")
    Page<PrescriptionDomain> findByPatientIdAndIsActiveTrue(@Param("patientId") Integer patientId, Pageable pageable);
    
    /**
     * Find prescription by ID with isActive naming (backward compatibility)
     */
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.id = :id AND p.active = true")
    Optional<PrescriptionDomain> findByIdAndIsActiveTrue(@Param("id") Integer id);
    
    /**
     * Check if prescription exists by ID with isActive naming (backward compatibility)
     */
    @Query("SELECT COUNT(p) > 0 FROM PrescriptionDomain p WHERE p.id = :id AND p.active = true")
    boolean existsByIdAndIsActiveTrue(@Param("id") Integer id);
}