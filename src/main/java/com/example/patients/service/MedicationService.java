package com.example.patients.service;

import com.fiuni.clinica.domain.patient.MedicationDomain;
import com.fiuni.clinica.dto.generated.MedicationRequest;
import com.fiuni.clinica.dto.generated.MedicationResponse;
import com.example.patients.mapper.MedicationMapper;
import com.example.patients.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for MedicationDomain entities
 * Handles business logic for medication operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MedicationService {
    
    private final MedicationRepository medicationRepository;
    private final MedicationMapper medicationMapper;
    
    /**
     * Get all active medications with pagination
     */
    @Transactional(readOnly = true)
    public Page<MedicationDomain> getAllMedications(Pageable pageable) {
        log.debug("Getting all active medications with pagination: {}", pageable);
        return medicationRepository.findByActiveTrue(pageable);
    }
    
    /**
     * Get medication by ID (only active medications)
     */
    @Transactional(readOnly = true)
    public Optional<MedicationDomain> getMedicationById(Integer id) {
        log.debug("Getting medication by ID: {}", id);
        return medicationRepository.findByIdAndActiveTrue(id);
    }
    
    /**
     * Search medications by name (medication name)
     */
    @Transactional(readOnly = true)
    public Page<MedicationDomain> searchMedicationsByName(String name, Pageable pageable) {
        log.debug("Searching medications by name: {} with pagination: {}", name, pageable);
        return medicationRepository.findByMedicationNameContainingIgnoreCaseAndActiveTrue(name, pageable);
    }
    
    /**
     * Search medications by manufacturer
     */
    @Transactional(readOnly = true)
    public Page<MedicationDomain> searchMedicationsByManufacturer(String manufacturer, Pageable pageable) {
        log.debug("Searching medications by manufacturer: {} with pagination: {}", manufacturer, pageable);
        return medicationRepository.findByManufacturerContainingIgnoreCaseAndActiveTrue(manufacturer, pageable);
    }
    
    /**
     * Search medications by generic name
     */
    @Transactional(readOnly = true)
    public Page<MedicationDomain> searchMedicationsByGenericName(String genericName, Pageable pageable) {
        log.debug("Searching medications by generic name: {} with pagination: {}", genericName, pageable);
        return medicationRepository.findByGenericNameContainingIgnoreCaseAndActiveTrue(genericName, pageable);
    }
    
    /**
     * Create a new medication using MedicationRequest
     */
    public MedicationResponse createMedication(MedicationRequest medicationRequest) {
        log.debug("Creating new medication from request: {}", medicationRequest);
        
        // Convert request to entity using mapper
        MedicationDomain medicationDomain = medicationMapper.requestToEntity(medicationRequest);
        
        // Save the entity
        MedicationDomain savedMedication = medicationRepository.save(medicationDomain);
        log.info("Created new medication with ID: {}", savedMedication.getId());
        
        // Convert back to response
        return medicationMapper.toDto(savedMedication);
    }
    
    /**
     * Update an existing medication using MedicationRequest
     */
    public MedicationResponse updateMedication(Integer id, MedicationRequest medicationRequest) {
        log.debug("Updating medication with ID: {}", id);
        
        MedicationDomain existingMedication = medicationRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Medication not found with ID: " + id));
        
        // Use mapper to update entity from request
        medicationMapper.updateEntityFromRequest(existingMedication, medicationRequest);
        
        // Save updated entity
        MedicationDomain updatedMedication = medicationRepository.save(existingMedication);
        log.info("Updated medication with ID: {}", updatedMedication.getId());
        
        // Convert back to response
        return medicationMapper.toDto(updatedMedication);
    }
    
    /**
     * Soft delete medication (set isActive to false)
     */
    public void deleteMedication(Integer id) {
        log.debug("Soft deleting medication with ID: {}", id);
        
        MedicationDomain medication = medicationRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Medication not found with ID: " + id));
        
        // Soft delete by setting active to false
        medication.setActive(false);
        medicationRepository.save(medication);
        
        log.info("Soft deleted medication with ID: {}", id);
    }
    
    /**
     * Check if a medication exists and is active
     */
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return medicationRepository.existsByIdAndActiveTrue(id);
    }
}