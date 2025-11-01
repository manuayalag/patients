package com.fiuni.patients.controller;

import com.fiuni.clinica.dto.generated.PrescriptionRequest;
import com.fiuni.clinica.dto.generated.PrescriptionCreateRequest;
import com.fiuni.clinica.dto.generated.PrescriptionResponse;
import com.fiuni.clinica.api.PrescriptionsApi;
import com.fiuni.clinica.dto.generated.PaginatedPrescriptionResponse;
import com.fiuni.clinica.dto.generated.PrescriptionMedicationRequest;
import com.fiuni.clinica.dto.generated.PrescriptionMedicationResponse;
import com.fiuni.patients.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller para gestión de prescripciones y medicamentos en prescripciones
 * Implementa PrescriptionsApi completamente
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, 
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
            allowedHeaders = "*")
public class PrescriptionController implements PrescriptionsApi {

    private final PrescriptionService prescriptionService;

    // ========================================
    // PRESCRIPTION CRUD OPERATIONS
    // ========================================

    @Override
    public ResponseEntity<PrescriptionResponse> createPrescription(@Valid PrescriptionCreateRequest prescriptionCreateRequest) {
        log.info("Request received to create prescription (create DTO)");

        try {
            // Create prescription header + medications (if present) in a single transactional call
            PrescriptionResponse prescription = prescriptionService.createPrescriptionWithMedications(prescriptionCreateRequest);
            log.info("Prescription created with ID: {}", prescription.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(prescription);
        } catch (Exception e) {
            log.error("Error creating prescription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<PaginatedPrescriptionResponse> getAllPrescriptions(
            @Min(0) @Valid Integer page, 
            @Min(1) @Max(100) @Valid Integer size, 
            @Valid Integer patientId, 
            @Valid Boolean isFilled) {
        
        log.info("Request to get all prescriptions - page: {}, size: {}, patientId: {}, isFilled: {}", 
                page, size, patientId, isFilled);
        
        try {
            Page<PrescriptionResponse> prescriptionPage = prescriptionService.getAllPrescriptions(
                    page != null ? page : 0, 
                    size != null ? size : 20, 
                    patientId, 
                    isFilled);

            PaginatedPrescriptionResponse response = new PaginatedPrescriptionResponse();
            response.setContent(prescriptionPage.getContent());
            response.setTotalPages(prescriptionPage.getTotalPages());
            response.setTotalElements((int) prescriptionPage.getTotalElements());
            response.setSize(prescriptionPage.getSize());
            response.setPage(prescriptionPage.getNumber());

            log.info("Page of prescriptions returned");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting prescriptions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<PrescriptionResponse> getPrescriptionById(Integer id) {
        log.info("Request to get prescription with ID: {}", id);
        
        try {
            Optional<PrescriptionResponse> prescription = prescriptionService.getPrescriptionById(id);
            
            if (prescription.isPresent()) {
                log.info("Prescription with ID: {} found", id);
                return ResponseEntity.ok(prescription.get());
            } else {
                log.warn("Prescription not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error getting prescription with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<PrescriptionResponse> updatePrescription(Integer id, @Valid PrescriptionRequest prescriptionRequest) {
        log.info("Request to update prescription with ID: {}", id);
        
        try {
            Optional<PrescriptionResponse> updatedPrescription = prescriptionService.updatePrescription(id, prescriptionRequest);
            
            if (updatedPrescription.isPresent()) {
                log.info("Prescription with ID: {} updated", id);
                return ResponseEntity.ok(updatedPrescription.get());
            } else {
                log.warn("Prescription not found for update with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error updating prescription with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Void> deletePrescription(Integer id) {
        log.info("Request to delete prescription with ID: {}", id);
        
        try {
            boolean deleted = prescriptionService.deletePrescription(id);
            
            if (deleted) {
                log.info("Prescription with ID: {} deleted", id);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Prescription not found for deletion with ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting prescription with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByPatient(Integer patientId) {
        log.info("Request to get prescriptions for patient ID: {}", patientId);
        
        try {
            List<PrescriptionResponse> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
            
            log.info("Found {} prescriptions for patient ID: {}", prescriptions.size(), patientId);
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            log.error("Error getting prescriptions for patient ID: {}", patientId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========================================
    // PRESCRIPTION-MEDICATION OPERATIONS
    // ========================================

    @Override
    public ResponseEntity<PrescriptionMedicationResponse> addMedicationToPrescription(
            Integer prescriptionId, @Valid PrescriptionMedicationRequest prescriptionMedicationRequest) {
        
        log.info("Request to add medication to prescription ID: {}", prescriptionId);
        
        try {
            PrescriptionMedicationResponse response = prescriptionService.addMedicationToPrescriptionByIds(
                    prescriptionId, prescriptionMedicationRequest.getMedicationId(), prescriptionMedicationRequest);
            
            log.info("Medication added to prescription ID: {}", prescriptionId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error adding medication to prescription ID: {}", prescriptionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<PrescriptionMedicationResponse>> getPrescriptionMedicationsList(Integer prescriptionId) {
        log.info("Request to get all medications for prescription ID: {}", prescriptionId);
        
        try {
            List<PrescriptionMedicationResponse> medications = prescriptionService.getPrescriptionMedicationsList(prescriptionId);
            
            log.info("Found {} medications for prescription ID: {}", medications.size(), prescriptionId);
            return ResponseEntity.ok(medications);
        } catch (Exception e) {
            log.error("Error getting medications for prescription ID: {}", prescriptionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<PrescriptionMedicationResponse> getPrescriptionMedicationDetail(
            Integer prescriptionId, Integer medicationId) {
        
        log.info("Request to get prescription-medication relationship: prescription ID: {}, medication ID: {}", 
                prescriptionId, medicationId);
        
        try {
            Optional<PrescriptionMedicationResponse> response = prescriptionService.getPrescriptionMedication(
                    prescriptionId, medicationId);
            
            if (response.isPresent()) {
                log.info("Prescription-medication relationship found");
                return ResponseEntity.ok(response.get());
            } else {
                log.warn("Prescription-medication relationship not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error getting prescription-medication relationship", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<PrescriptionMedicationResponse> updatePrescriptionMedication(
            Integer prescriptionId, Integer medicationId, @Valid PrescriptionMedicationRequest prescriptionMedicationRequest) {
        
        log.info("Request to update prescription-medication: prescription ID: {}, medication ID: {}", 
                prescriptionId, medicationId);
        
        try {
            Optional<PrescriptionMedicationResponse> response = prescriptionService.updatePrescriptionMedicationByIds(
                    prescriptionId, medicationId, prescriptionMedicationRequest);
            
            if (response.isPresent()) {
                log.info("Prescription-medication updated successfully");
                return ResponseEntity.ok(response.get());
            } else {
                log.warn("Prescription-medication relationship not found for update");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error updating prescription-medication relationship", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Void> removeMedicationFromPrescription(Integer prescriptionId, Integer medicationId) {
        log.info("Request to remove medication ID: {} from prescription ID: {}", medicationId, prescriptionId);
        
        try {
            boolean removed = prescriptionService.removeMedicationFromPrescriptionByIds(prescriptionId, medicationId);
            
            if (removed) {
                log.info("Medication ID: {} removed from prescription ID: {}", medicationId, prescriptionId);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Prescription-medication relationship not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error removing medication from prescription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

