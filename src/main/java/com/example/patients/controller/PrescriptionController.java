package com.example.patients.controller;

import com.example.patients.service.PrescriptionService;
import com.fiuni.clinica.domain.patient.PrescriptionDomain;
import com.fiuni.clinica.domain.patient.MedicationDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for Prescription management
 * Handles prescription operations and many-to-many relationships with medications
 */
@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping
    public ResponseEntity<Page<PrescriptionDomain>> getAllPrescriptions(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting all prescriptions with pagination: {}", pageable);
        
        try {
            Page<PrescriptionDomain> prescriptions = prescriptionService.getAllPrescriptions(pageable);
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            log.error("Error getting all prescriptions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDomain> getPrescriptionById(@PathVariable Integer id) {
        log.info("Getting prescription by ID: {}", id);
        
        try {
            return prescriptionService.getPrescriptionById(id)
                    .map(prescription -> ResponseEntity.ok(prescription))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting prescription by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<PrescriptionDomain> createPrescription(@Valid @RequestBody PrescriptionDomain prescriptionData) {
        log.info("Creating new prescription for patient ID: {}", prescriptionData.getPatient() != null ? prescriptionData.getPatient().getId() : "null");
        
        try {
            PrescriptionDomain createdPrescription = prescriptionService.createPrescription(prescriptionData);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPrescription);
        } catch (RuntimeException e) {
            log.error("Error creating prescription: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error creating prescription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionDomain> updatePrescription(
            @PathVariable Integer id, 
            @Valid @RequestBody PrescriptionDomain prescriptionData) {
        log.info("Updating prescription with ID: {}", id);
        
        try {
            PrescriptionDomain updatedPrescription = prescriptionService.updatePrescription(id, prescriptionData);
            return ResponseEntity.ok(updatedPrescription);
        } catch (RuntimeException e) {
            log.error("Error updating prescription: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating prescription with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Integer id) {
        log.info("Soft deleting prescription with ID: {}", id);
        
        try {
            prescriptionService.deletePrescription(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting prescription: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting prescription with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Many-to-Many relationship endpoints
    @GetMapping("/{prescriptionId}/medications")
    public ResponseEntity<List<MedicationDomain>> getPrescriptionMedications(@PathVariable Integer prescriptionId) {
        log.info("Getting medications for prescription ID: {}", prescriptionId);
        
        try {
            List<MedicationDomain> medications = prescriptionService.getPrescriptionMedications(prescriptionId);
            return ResponseEntity.ok(medications);
        } catch (RuntimeException e) {
            log.error("Error getting medications: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting medications for prescription ID: {}", prescriptionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{prescriptionId}/medications/{medicationId}")
    public ResponseEntity<Void> addMedicationToPrescription(
            @PathVariable Integer prescriptionId, 
            @PathVariable Integer medicationId) {
        log.info("Adding medication ID {} to prescription ID {}", medicationId, prescriptionId);
        
        try {
            prescriptionService.addMedicationToPrescription(prescriptionId, medicationId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error adding medication to prescription: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error adding medication {} to prescription {}", medicationId, prescriptionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{prescriptionId}/medications/{medicationId}")
    public ResponseEntity<Void> removeMedicationFromPrescription(
            @PathVariable Integer prescriptionId, 
            @PathVariable Integer medicationId) {
        log.info("Removing medication ID {} from prescription ID {}", medicationId, prescriptionId);
        
        try {
            prescriptionService.removeMedicationFromPrescription(prescriptionId, medicationId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error removing medication from prescription: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error removing medication {} from prescription {}", medicationId, prescriptionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<Page<PrescriptionDomain>> getPrescriptionsByPatient(
            @PathVariable Integer patientId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting prescriptions for patient ID: {} with pagination: {}", patientId, pageable);
        
        try {
            Page<PrescriptionDomain> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId, pageable);
            return ResponseEntity.ok(prescriptions);
        } catch (Exception e) {
            log.error("Error getting prescriptions for patient ID: {}", patientId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}