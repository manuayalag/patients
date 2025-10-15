package com.fiuni.patients.service;

import com.fiuni.clinica.domain.patient.PatientDomain;
import com.fiuni.clinica.dto.generated.PatientRequest;
import com.fiuni.clinica.dto.generated.PatientResponse;
import com.fiuni.patients.mapper.PatientMapper;
import com.fiuni.patients.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    /**
     * Obtener todos los pacientes con paginación
     */
    @Transactional(readOnly = true)
    public Page<PatientResponse> getAllPatients(Pageable pageable) {
        log.info("Getting all patients with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<PatientDomain> patients = patientRepository.findAllActive(pageable);
        
        log.info("Found {} patients", patients.getTotalElements());
        
        return patients.map(patientMapper::toDto);
    }

    /**
     * Obtener paciente por ID
     */
    @Transactional(readOnly = true)
    public Optional<PatientResponse> getPatientById(Integer id) {
        log.info("Getting patient by ID: {}", id);
        
        Optional<PatientDomain> patient = patientRepository.findByIdAndActiveTrue(id);
        
        if (patient.isPresent()) {
            log.info("Patient found with ID: {}", id);
            return Optional.of(patientMapper.toDto(patient.get()));
        } else {
            log.warn("Patient not found with ID: {}", id);
            return Optional.empty();
        }
    }

    /**
     * Crear nuevo paciente
     */
    public PatientResponse createPatient(PatientRequest request) {
        log.info("Creating new patient: {} {}", request.getFirstName(), request.getLastName());
        
        PatientDomain patient = patientMapper.toEntity(request);
        PatientDomain savedPatient = patientRepository.save(patient);
        
        log.info("Patient created successfully with ID: {}", savedPatient.getId());
        
        return patientMapper.toDto(savedPatient);
    }

    /**
     * Actualizar paciente existente
     */
    public Optional<PatientResponse> updatePatient(Integer id, PatientRequest request) {
        log.info("Updating patient with ID: {}", id);
        
        Optional<PatientDomain> existingPatient = patientRepository.findByIdAndActiveTrue(id);
        
        if (existingPatient.isPresent()) {
            PatientDomain patient = existingPatient.get();
            patientMapper.updateEntityFromRequest(patient, request);
            
            PatientDomain updatedPatient = patientRepository.save(patient);
            
            log.info("Patient updated successfully with ID: {}", id);
            return Optional.of(patientMapper.toDto(updatedPatient));
        } else {
            log.warn("Cannot update - Patient not found with ID: {}", id);
            return Optional.empty();
        }
    }

    /**
     * Eliminar paciente (soft delete)
     */
    public boolean deletePatient(Integer id) {
        log.info("Deleting patient with ID: {}", id);
        
        Optional<PatientDomain> patient = patientRepository.findByIdAndActiveTrue(id);
        
        if (patient.isPresent()) {
            PatientDomain patientToDelete = patient.get();
            patientToDelete.setActive(false);
            patientRepository.save(patientToDelete);
            
            log.info("Patient deleted successfully with ID: {}", id);
            return true;
        } else {
            log.warn("Cannot delete - Patient not found with ID: {}", id);
            return false;
        }
    }

    /**
     * Buscar pacientes por criterios
     */
    @Transactional(readOnly = true)
    public List<PatientResponse> searchPatients(String firstName, String lastName, String document, String bloodType) {
        log.info("Searching patients with term: {}", firstName);
        List<PatientDomain> patients = patientRepository.searchPatients(firstName);
        log.info("Search found {} patients", patients.size());
        return patientMapper.toResponseList(patients);
    }

    /**
     * Contar pacientes activos
     */
    @Transactional(readOnly = true)
    public long countActivePatients() {
        log.debug("Counting active patients");
        return patientRepository.countActivePatients();
    }
    
    // ================== Controller Support Methods ==================
    

}