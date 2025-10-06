package com.example.patients.repository;

import com.fiuni.clinica.domain.patient.PatientDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<PatientDomain, Integer> {
    
    // Buscar paciente por email
    Optional<PatientDomain> findByEmail(String email);
    
    // Buscar pacientes por nombre (case insensitive)
    @Query("SELECT p FROM PatientDomain p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<PatientDomain> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    // Buscar pacientes por género
    List<PatientDomain> findByGender(String gender);
    
    // Buscar pacientes activos (si tienes campo de estado)
    // Comentado temporalmente - verificar que campos tiene PatientDomain en v0.0.10
    // @Query("SELECT p FROM PatientDomain p WHERE p.active = true")
    // Page<PatientDomain> findActivePatients(Pageable pageable);
    
    // Buscar pacientes con prescripciones
    @Query("SELECT DISTINCT p FROM PatientDomain p JOIN p.prescriptions pr")
    Page<PatientDomain> findPatientsWithPrescriptions(Pageable pageable);
}