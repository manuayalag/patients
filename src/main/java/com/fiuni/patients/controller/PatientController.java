package com.fiuni.patients.controller;

import com.fiuni.clinica.api.PatientsApi;
import com.fiuni.clinica.dto.generated.PaginatedPatientResponse;
import com.fiuni.clinica.dto.generated.PatientRequest;
import com.fiuni.clinica.dto.generated.PatientResponse;
import com.fiuni.clinica.dto.generated.PatientSearchRequest;
import com.fiuni.clinica.dto.generated.PrescriptionResponse;
import com.fiuni.patients.service.PatientService;
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
public class PatientController implements PatientsApi {
    private final PatientService patientService;
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);

    @Override
    public ResponseEntity<PatientResponse> createPatient(PatientRequest request) {
        logger.info("Request received to create patient: {} {}", 
                request.getFirstName(), request.getLastName());
        
        PatientResponse response = patientService.createPatient(request);
        
        logger.info("Patient created with ID: {}", response.getId());
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<Void> deletePatient(Integer patientId) {
        logger.info("Request to delete patient with ID: {}", patientId);
        
        boolean deleted = patientService.deletePatient(patientId);
        
        if (deleted) {
            logger.info("Patient with ID: {} soft deleted", patientId);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Cannot delete - Patient not found with ID: {}", patientId);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<PaginatedPatientResponse> getAllPatients(Integer page, Integer size, String sort) {
        logger.info("Request to get all patients - page: {}, size: {}, sort: {}", page, size, sort);
        
        // Create pageable and get page from service
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20,
                org.springframework.data.domain.Sort.by(sort != null ? sort : "id")
        );
        org.springframework.data.domain.Page<PatientResponse> patientsPage = patientService.getAllPatients(pageable);
        
        // Convert to PaginatedPatientResponse
        PaginatedPatientResponse response = new PaginatedPatientResponse();
        response.setContent(patientsPage.getContent());
        response.setTotalElements((int) patientsPage.getTotalElements());
        response.setTotalPages(patientsPage.getTotalPages());
        response.setPage(patientsPage.getNumber());
        response.setSize(patientsPage.getSize());
        
        logger.info("Page of patients returned");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PatientResponse> getPatientById(Integer patientId) {
        logger.info("Request to get patient with ID: {}", patientId);
        
        java.util.Optional<PatientResponse> patient = patientService.getPatientById(patientId);
        
        if (patient.isPresent()) {
            logger.info("Patient with ID: {} returned", patientId);
            return ResponseEntity.ok(patient.get());
        } else {
            logger.warn("Patient not found with ID: {}", patientId);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<List<PrescriptionResponse>> getPatientPrescriptions(Integer patientId) {
        logger.info("Request to get prescriptions for patient ID: {}", patientId);
        
        // This would normally call a prescription service or use inter-service communication
        List<PrescriptionResponse> prescriptions = List.of();
        
        logger.info("Returning {} prescriptions for patient {}", prescriptions.size(), patientId);
        return ResponseEntity.ok(prescriptions);
    }

    @Override
    public ResponseEntity<PaginatedPatientResponse> searchPatients(com.fiuni.clinica.dto.generated.PatientSearchRequest searchRequest, Integer page, String sort) {
        logger.info("Request to search patients - searchRequest: {}, page: {}, sort: {}", 
                searchRequest, page, sort);
        
        // Extraer campos del searchRequest para mantener compatibilidad con el servicio
        String firstName = searchRequest != null ? searchRequest.getFirstName() : null;
        String lastName = searchRequest != null ? searchRequest.getLastName() : null;
        String document = searchRequest != null ? searchRequest.getDocumentNumber() : null;
        String bloodType = searchRequest != null && searchRequest.getBloodType() != null ? searchRequest.getBloodType().name() : null;
        
        List<PatientResponse> patients = patientService.searchPatients(firstName, lastName, document, bloodType);
        
        // Convertir List a PaginatedPatientResponse para compatibilidad con la interfaz
        PaginatedPatientResponse response = new PaginatedPatientResponse();
        response.setContent(patients);
        response.setTotalElements(patients.size());
        response.setTotalPages(1); // Como es una lista simple, solo hay 1 página
        response.setPage(page != null ? page : 0);
        response.setSize(patients.size());
        
        logger.info("Search returned {} patients", patients.size());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PatientResponse> updatePatient(Integer patientId, PatientRequest request) {
        logger.info("Request to update patient with ID: {}", patientId);
        
        java.util.Optional<PatientResponse> updated = patientService.updatePatient(patientId, request);
        
        if (updated.isPresent()) {
            logger.info("Patient with ID: {} updated", patientId);
            return ResponseEntity.ok(updated.get());
        } else {
            logger.warn("Cannot update - Patient not found with ID: {}", patientId);
            return ResponseEntity.notFound().build();
        }
    }
}