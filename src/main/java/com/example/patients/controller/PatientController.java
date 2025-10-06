package com.example.patients.controller;

import com.example.patients.service.PatientService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Importar las clases del JAR de Clinica
import com.fiuni.clinica.domain.patient.PatientDomain;
import com.fiuni.clinica.domain.patient.PrescriptionDomain;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<Page<PatientDomain>> getAllPatients(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting all patients with pagination: {}", pageable);
        
        try {
            Page<PatientDomain> patients = patientService.getAllPatients(pageable);
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            log.error("Error getting all patients", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDomain> getPatientById(@PathVariable Integer id) {
        log.info("Getting patient by ID: {}", id);
        
        try {
            return patientService.getPatientById(id)
                    .map(patient -> ResponseEntity.ok(patient))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting patient by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<PatientDomain> createPatient(@Valid @RequestBody PatientDomain patientData) {
        log.info("Creating new patient with email: {}", patientData.getEmail());
        
        try {
            PatientDomain createdPatient = patientService.createPatient(patientData);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
        } catch (RuntimeException e) {
            log.error("Error creating patient: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error creating patient", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDomain> updatePatient(
            @PathVariable Integer id, 
            @Valid @RequestBody PatientDomain patientData) {
        log.info("Updating patient with ID: {}", id);
        
        try {
            PatientDomain updatedPatient = patientService.updatePatient(id, patientData);
            return ResponseEntity.ok(updatedPatient);
        } catch (RuntimeException e) {
            log.error("Error updating patient: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating patient with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Integer id) {
        log.info("Deleting patient with ID: {}", id);
        
        try {
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting patient: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting patient with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoints específicos para el microservicio de pacientes
    @GetMapping("/{patientId}/prescriptions")
    public ResponseEntity<List<PrescriptionDomain>> getPatientPrescriptions(@PathVariable Integer patientId) {
        log.info("Getting prescriptions for patient ID: {}", patientId);
        
        try {
            List<PrescriptionDomain> prescriptions = patientService.getPatientPrescriptions(patientId);
            return ResponseEntity.ok(prescriptions);
        } catch (RuntimeException e) {
            log.error("Error getting prescriptions: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting prescriptions for patient ID: {}", patientId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PatientDomain>> searchPatientsByName(
            @RequestParam String name,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Searching patients by name: {}", name);
        
        try {
            Page<PatientDomain> patients = patientService.searchPatientsByName(name, pageable);
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            log.error("Error searching patients by name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PatientDomain> getPatientByEmail(@PathVariable String email) {
        log.info("Getting patient by email: {}", email);
        
        try {
            return patientService.getPatientByEmail(email)
                    .map(patient -> ResponseEntity.ok(patient))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting patient by email: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}