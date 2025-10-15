package com.fiuni.patients.service;

import com.fiuni.clinica.domain.patient.MedicationDomain;
import com.fiuni.clinica.dto.generated.MedicationRequest;
import com.fiuni.clinica.dto.generated.MedicationResponse;
import com.fiuni.patients.mapper.MedicationMapper;
import com.fiuni.patients.repository.MedicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service para gestión de medicamentos
 */
@Service
@Slf4j
public class MedicationService extends AbstractBaseService<MedicationDomain, MedicationRequest, MedicationResponse> {

    private final MedicationRepository medicationRepository; // keep for specialized queries
    private final MedicationMapper medicationMapper;

    public MedicationService(MedicationRepository medicationRepository, MedicationMapper medicationMapper) {
        super(medicationRepository, medicationMapper);
        this.medicationRepository = medicationRepository;
        this.medicationMapper = medicationMapper;
    }

    /**
     * Obtener todos los medicamentos con paginación
     */
    @Transactional(readOnly = true)
    public Page<MedicationResponse> getAllMedications(Pageable pageable) {
        log.info("Getting all medications with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        
    Page<MedicationDomain> medications = medicationRepository.findByIsActiveTrue(pageable);
        
        log.info("Found {} medications", medications.getTotalElements());
        
        return medications.map(medicationMapper::toDto);
    }

    /**
     * Obtener medicamento por ID
     */
    @Transactional(readOnly = true)
    public Optional<MedicationResponse> getMedicationById(Integer id) {
        log.info("Getting medication by ID: {}", id);
        
    Optional<MedicationDomain> medication = medicationRepository.findByIdAndIsActiveTrue(id);
        
        if (medication.isPresent()) {
            log.info("Found medication with ID: {}", id);
            return Optional.of(medicationMapper.toDto(medication.get()));
        } else {
            log.warn("Medication not found with ID: {}", id);
            return Optional.empty();
        }
    }

    /**
     * Crear nuevo medicamento
     */
    public MedicationResponse createMedication(MedicationRequest request) {
        log.info("Creating new medication with request data");
        
    MedicationDomain medication = medicationMapper.toEntity(request);
    MedicationDomain savedMedication = medicationRepository.save(medication);
        
        log.info("Medication created successfully with ID: {}", savedMedication.getId());
        
        return medicationMapper.toDto(savedMedication);
    }

    /**
     * Actualizar medicamento existente
     */
    @Transactional
    public Optional<MedicationResponse> updateMedication(Integer id, MedicationRequest request) {
        log.info("Updating medication with ID: {}", id);
        
    Optional<MedicationDomain> existingMedication = medicationRepository.findByIdAndIsActiveTrue(id);
        
        if (!existingMedication.isPresent()) {
            log.warn("Medication not found with ID: {}", id);
            return Optional.empty();
        }
        
        MedicationDomain medication = existingMedication.get();
        medicationMapper.updateEntity(medication, request);
        
        MedicationDomain savedMedication = medicationRepository.save(medication);
        
        log.info("Medication updated successfully with ID: {}", id);
        
        return Optional.of(medicationMapper.toDto(savedMedication));
    }

    /**
     * Eliminar medicamento (soft delete)
     */
    @Transactional
    public boolean deleteMedication(Integer id) {
        log.info("Deleting medication with ID: {}", id);
        
    Optional<MedicationDomain> medication = medicationRepository.findByIdAndIsActiveTrue(id);
        
        if (medication.isPresent()) {
            MedicationDomain medicationEntity = medication.get();
            medicationEntity.setIsActive(false);
            medicationRepository.save(medicationEntity);
            
            log.info("Medication deleted successfully with ID: {}", id);
            return true;
        } else {
            log.warn("Medication not found for deletion with ID: {}", id);
            return false;
        }
    }

    /**
     * Buscar medicamentos por término de búsqueda
     */
    @Transactional(readOnly = true)
    public List<MedicationResponse> searchMedications(String searchTerm) {
        log.info("Searching medications with search term: {}", searchTerm);
        
        // Usar paginación con límite alto para simular lista completa
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 1000);
        org.springframework.data.domain.Page<MedicationDomain> medicationsPage = medicationRepository.searchByTerm(searchTerm, pageable);
        
        log.info("Search found {} medications", medicationsPage.getTotalElements());
        
        return medicationMapper.toResponseList(medicationsPage.getContent());
    }

    /**
     * Buscar medicamentos por criterios múltiples - delegado a búsqueda simple
     */
    @Transactional(readOnly = true)
    public List<MedicationResponse> searchMedications(String name, String genericName, String medicationType, String manufacturer) {
        log.info("Searching medications with criteria - name: {}, genericName: {}, medicationType: {}, manufacturer: {}", 
                name, genericName, medicationType, manufacturer);
        
        // Usar el término más relevante para la búsqueda
        String searchTerm = name != null ? name : 
                           genericName != null ? genericName : 
                           medicationType != null ? medicationType : 
                           manufacturer != null ? manufacturer : "";
        
        return searchMedications(searchTerm);
    }

    /**
     * Contar medicamentos activos
     */
    @Transactional(readOnly = true)
    public long countActiveMedications() {
        log.debug("Counting active medications");
        return medicationRepository.count();
    }

}