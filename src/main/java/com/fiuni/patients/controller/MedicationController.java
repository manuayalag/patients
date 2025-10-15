package com.fiuni.patients.controller;

import com.fiuni.clinica.api.MedicationsApi;
import com.fiuni.clinica.dto.generated.MedicationRequest;
import com.fiuni.clinica.dto.generated.MedicationResponse;
import com.fiuni.clinica.dto.generated.MedicationSearchRequest;
import com.fiuni.clinica.dto.generated.PaginatedMedicationResponse;
import com.fiuni.patients.service.MedicationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, 
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
            allowedHeaders = "*")
public class MedicationController implements MedicationsApi {
    private final MedicationService medicationService;
    private static final Logger logger = LoggerFactory.getLogger(MedicationController.class);

    @Override
    public ResponseEntity<MedicationResponse> createMedication(MedicationRequest medicationRequest) {
        logger.info("Request received to create medication");
        
        MedicationResponse response = medicationService.createMedication(medicationRequest);
        
        logger.info("Medication created with ID: {}", response.getId());
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteMedication(Integer id) {
        logger.info("Request to delete medication with ID: {}", id);
        
        boolean deleted = medicationService.deleteMedication(id);
        
        if (deleted) {
            logger.info("Medication with ID: {} soft deleted", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Cannot delete - Medication not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<PaginatedMedicationResponse> getAllMedications(Integer page, Integer size, String medicationType, String manufacturer) {
        logger.info("Request to get all medications - page: {}, size: {}, medicationType: {}, manufacturer: {}", 
                page, size, medicationType, manufacturer);
        
        // Create pageable and get page from service
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                org.springframework.data.domain.Sort.by("id")
        );
        org.springframework.data.domain.Page<MedicationResponse> medicationsPage = medicationService.getAllMedications(pageable);
        
        // Convert to PaginatedMedicationResponse
        PaginatedMedicationResponse response = new PaginatedMedicationResponse();
        response.setContent(medicationsPage.getContent());
        response.setTotalElements((int) medicationsPage.getTotalElements());
        response.setTotalPages(medicationsPage.getTotalPages());
        response.setPage(medicationsPage.getNumber());
        response.setSize(medicationsPage.getSize());
        
        logger.info("Page of medications returned");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MedicationResponse> getMedicationById(Integer id) {
        logger.info("Request to get medication with ID: {}", id);
        
        java.util.Optional<MedicationResponse> medication = medicationService.getMedicationById(id);
        
        if (medication.isPresent()) {
            logger.info("Medication with ID: {} returned", id);
            return ResponseEntity.ok(medication.get());
        } else {
            logger.warn("Medication not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<List<MedicationResponse>> searchMedications(MedicationSearchRequest medicationSearchRequest) {
        logger.info("Request to search medications - searchRequest: {}", medicationSearchRequest);
        
        // Extraer campos del searchRequest usando los campos reales
        String name = medicationSearchRequest != null ? medicationSearchRequest.getName() : null;
        String genericName = medicationSearchRequest != null ? medicationSearchRequest.getGenericName() : null;
        String medicationType = medicationSearchRequest != null ? medicationSearchRequest.getMedicationType() : null;
        String manufacturer = medicationSearchRequest != null ? medicationSearchRequest.getManufacturer() : null;
        
        List<MedicationResponse> medications = medicationService.searchMedications(name, genericName, medicationType, manufacturer);
        
        logger.info("Search returned {} medications", medications.size());
        return ResponseEntity.ok(medications);
    }

    @Override
    public ResponseEntity<MedicationResponse> updateMedication(Integer id, MedicationRequest medicationRequest) {
        logger.info("Request to update medication with ID: {}", id);
        
        java.util.Optional<MedicationResponse> updated = medicationService.updateMedication(id, medicationRequest);
        
        if (updated.isPresent()) {
            logger.info("Medication with ID: {} updated", id);
            return ResponseEntity.ok(updated.get());
        } else {
            logger.warn("Cannot update - Medication not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}