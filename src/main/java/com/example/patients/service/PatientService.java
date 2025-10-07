package com.example.patients.service;

import com.fiuni.clinica.domain.patient.PatientDomain;
import com.fiuni.clinica.domain.patient.PrescriptionDomain;
import com.example.patients.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PatientService {
    
    private final PatientRepository patientRepository;
    
    /**
     * Obtener todos los pacientes activos con paginación
     */
    @Transactional(readOnly = true)
    public Page<PatientDomain> getAllPatients(Pageable pageable) {
        log.debug("Getting all active patients with pagination: {}", pageable);
        return patientRepository.findByIsActiveTrue(pageable);
    }
    
    /**
     * Obtener paciente activo por ID
     */
    @Transactional(readOnly = true)
    public Optional<PatientDomain> getPatientById(Integer id) {
        log.debug("Getting active patient by ID: {}", id);
        return patientRepository.findByIdAndIsActiveTrue(id);
    }
    
    /**
     * Obtener paciente por email
     */
    @Transactional(readOnly = true)
    public Optional<PatientDomain> getPatientByEmail(String email) {
        log.debug("Getting patient by email: {}", email);
        return patientRepository.findByEmailAndIsActiveTrue(email);
    }
    
    /**
     * Buscar pacientes por nombre
     */
    @Transactional(readOnly = true)
    public Page<PatientDomain> searchPatientsByName(String name, Pageable pageable) {
        log.debug("Searching patients by name: {} with pagination: {}", name, pageable);
        return patientRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name, pageable);
    }
    
    /**
     * Crear nuevo paciente
     */
    public PatientDomain createPatient(PatientDomain patient) {
        log.debug("Creating new patient: {}", patient.getEmail());
        
        // Validar que el email no exista
        if (patientRepository.findByEmailAndIsActiveTrue(patient.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un paciente con el email: " + patient.getEmail());
        }
        
        return patientRepository.save(patient);
    }
    
    /**
     * Actualizar paciente existente
     */
    public PatientDomain updatePatient(Integer id, PatientDomain patientData) {
        log.debug("Updating patient with ID: {}", id);
        
        PatientDomain existingPatient = patientRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
        
        // Actualizar campos disponibles en PatientDomain
        existingPatient.setFirstName(patientData.getFirstName());
        existingPatient.setLastName(patientData.getLastName());
        existingPatient.setEmail(patientData.getEmail());
        existingPatient.setGender(patientData.getGender());
        
        // Otros campos pueden estar disponibles dependiendo de la implementación de PatientDomain
        // existingPatient.setPhone(patientData.getPhone());
        // existingPatient.setDateOfBirth(patientData.getDateOfBirth());
        // existingPatient.setAddress(patientData.getAddress());
        
        return patientRepository.save(existingPatient);
    }
    
    /**
     * Eliminar paciente (soft delete - marca como inactivo)
     */
    public void deletePatient(Integer id) {
        log.debug("Soft deleting patient with ID: {}", id);
        
        PatientDomain patient = patientRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
        
        // Soft delete usando el campo isActive de PersonDomain
        patient.setActive(false);
        patientRepository.save(patient);
        
        log.info("Patient with ID {} successfully soft deleted (marked as inactive)", id);
    }
    
    /**
     * Obtener prescripciones de un paciente
     */
    @Transactional(readOnly = true)
    public List<PrescriptionDomain> getPatientPrescriptions(Integer patientId) {
        log.debug("Getting prescriptions for patient ID: {}", patientId);
        
        PatientDomain patient = patientRepository.findByIdAndIsActiveTrue(patientId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + patientId));
        
        return patient.getPrescriptions();
    }
    
    /**
     * Verificar si existe un paciente
     */
    @Transactional(readOnly = true)
    public boolean existsPatient(Integer id) {
        return patientRepository.existsByIdAndActiveTrue(id);
    }
}