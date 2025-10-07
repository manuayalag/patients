package com.example.patients.repository;

import com.example.patients.repository.base.IBaseRepository;
import com.fiuni.clinica.domain.patient.PatientDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends IBaseRepository<PatientDomain> {
    
    // Métodos específicos para PatientDomain (que usa isActive como boolean)
    @Query("SELECT p FROM PatientDomain p WHERE p.id = :id AND p.isActive = true")
    Optional<PatientDomain> findByIdAndIsActiveTrue(@Param("id") Integer id);
    
    @Query("SELECT p FROM PatientDomain p WHERE p.isActive = true")
    Page<PatientDomain> findByIsActiveTrue(Pageable pageable);
    
    // Buscar paciente por email (solo activos)
    @Query("SELECT p FROM PatientDomain p WHERE p.email = :email AND p.isActive = true")
    Optional<PatientDomain> findByEmailAndIsActiveTrue(@Param("email") String email);
    
    // Buscar pacientes por nombre (case insensitive, solo activos)
    @Query("SELECT p FROM PatientDomain p WHERE p.isActive = true AND (" +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<PatientDomain> findByNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name, Pageable pageable);
    
    // Buscar pacientes por género (solo activos)
    @Query("SELECT p FROM PatientDomain p WHERE p.gender = :gender AND p.isActive = true")
    List<PatientDomain> findByGenderAndIsActiveTrue(@Param("gender") String gender);
    
    // Buscar pacientes con prescripciones (solo activos)
    @Query("SELECT DISTINCT p FROM PatientDomain p JOIN p.prescriptions pr WHERE p.isActive = true")
    Page<PatientDomain> findPatientsWithPrescriptionsAndIsActiveTrue(Pageable pageable);
    
    // Verificar si existe un paciente activo por ID
    @Query("SELECT COUNT(p) > 0 FROM PatientDomain p WHERE p.id = :id AND p.isActive = true")
    boolean existsByIdAndActiveTrue(@Param("id") Integer id);
}