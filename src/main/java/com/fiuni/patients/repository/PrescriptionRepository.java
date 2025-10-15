package com.fiuni.patients.repository;

import com.fiuni.clinica.domain.patient.PrescriptionDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para PrescriptionDomain usando entidades del JAR externo
 */
@Repository
public interface PrescriptionRepository extends JpaRepository<PrescriptionDomain, Integer> {

    /**
     * Buscar prescripciones activas
     */
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.active = true")
    Page<PrescriptionDomain> findAllActive(Pageable pageable);

    /**
     * Buscar prescripción por ID y activa
     */
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.id = :id AND p.active = true")
    Optional<PrescriptionDomain> findByIdAndActiveTrue(@Param("id") Integer id);

    /**
     * Buscar prescripciones por paciente simplificado
     */
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.active = true")
    List<PrescriptionDomain> findByPatientId(@Param("patientId") Integer patientId);

    /**
     * Buscar prescripciones por paciente con paginación
     */
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.active = true")
    Page<PrescriptionDomain> findByPatientIdAndActiveTrue(@Param("patientId") Integer patientId, Pageable pageable);

    /**
     * Contar prescripciones activas
     */
    @Query("SELECT COUNT(p) FROM PrescriptionDomain p WHERE p.active = true")
    long countActivePrescriptions();

    /**
     * Contar prescripciones por paciente
     */
    @Query("SELECT COUNT(p) FROM PrescriptionDomain p WHERE p.active = true")
    long countByPatientId(@Param("patientId") Integer patientId);

    /**
     * Buscar prescripciones por término general con paginación
     */
    @Query("SELECT p FROM PrescriptionDomain p WHERE p.active = true")
    Page<PrescriptionDomain> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}