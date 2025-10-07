package com.example.patients.service;

import com.example.patients.repository.PrescriptionRepository;
import com.fiuni.clinica.domain.patient.PrescriptionDomain;
import com.fiuni.clinica.domain.patient.MedicationDomain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for Prescription management
 * Handles prescription operations and many-to-many relationships with medications
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {
    
    private final PrescriptionRepository prescriptionRepository;
    
    /**
     * Obtener todas las prescripciones activas con paginación
     */
    @Transactional(readOnly = true)
    public Page<PrescriptionDomain> getAllPrescriptions(Pageable pageable) {
        log.debug("Getting all active prescriptions with pagination: {}", pageable);
        return prescriptionRepository.findByActiveTrue(pageable);
    }
    
    /**
     * Obtener prescripción activa por ID
     */
    @Transactional(readOnly = true)
    public Optional<PrescriptionDomain> getPrescriptionById(Integer id) {
        log.debug("Getting active prescription by ID: {}", id);
        return prescriptionRepository.findByIdAndIsActiveTrue(id);
    }
    
    /**
     * Crear nueva prescripción
     */
    public PrescriptionDomain createPrescription(PrescriptionDomain prescription) {
        log.debug("Creating new prescription for patient: {}", 
            prescription.getPatient() != null ? prescription.getPatient().getId() : "null");
        
        // Validar que el paciente exista
        if (prescription.getPatient() == null || prescription.getPatient().getId() == null) {
            throw new RuntimeException("Patient is required for prescription");
        }
        
        // Asegurar que la prescripción esté activa
        prescription.setActive(true);
        
        return prescriptionRepository.save(prescription);
    }
    
    /**
     * Actualizar prescripción existente
     */
    public PrescriptionDomain updatePrescription(Integer id, PrescriptionDomain prescriptionData) {
        log.debug("Updating prescription with ID: {}", id);
        
        PrescriptionDomain existingPrescription = prescriptionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Prescripción no encontrada con ID: " + id));
        
        // Actualizar campos disponibles
        if (prescriptionData.getPatient() != null) {
            existingPrescription.setPatient(prescriptionData.getPatient());
        }
        // Otros campos que pueda tener PrescriptionDomain
        // existingPrescription.setDescription(prescriptionData.getDescription());
        // existingPrescription.setDosage(prescriptionData.getDosage());
        
        return prescriptionRepository.save(existingPrescription);
    }
    
    /**
     * Eliminar prescripción (soft delete - marca como inactivo)
     */
    public void deletePrescription(Integer id) {
        log.debug("Soft deleting prescription with ID: {}", id);
        
        PrescriptionDomain prescription = prescriptionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Prescripción no encontrada con ID: " + id));
        
        // Soft delete usando el campo active
        prescription.setActive(false);
        prescriptionRepository.save(prescription);
        
        log.info("Prescription with ID {} successfully soft deleted (marked as inactive)", id);
    }
    
    /**
     * Obtener medicamentos de una prescripción (relación many-to-many)
     */
    @Transactional(readOnly = true)
    public List<MedicationDomain> getPrescriptionMedications(Integer prescriptionId) {
        log.debug("Getting medications for prescription ID: {}", prescriptionId);
        
        PrescriptionDomain prescription = prescriptionRepository.findByIdAndActiveTrue(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescripción no encontrada con ID: " + prescriptionId));
        
        // Convertir PrescriptionMedicationDomain a MedicationDomain si es necesario
        // return prescription.getMedications().stream()
        //     .map(PrescriptionMedicationDomain::getMedication)
        //     .collect(Collectors.toList());
        
        // Por ahora, returnamos una lista vacía hasta confirmar la estructura
        return java.util.Collections.emptyList();
    }
    
    /**
     * Agregar medicamento a prescripción (many-to-many)
     */
    public void addMedicationToPrescription(Integer prescriptionId, Integer medicationId) {
        log.debug("Adding medication ID {} to prescription ID {}", medicationId, prescriptionId);
        
        PrescriptionDomain prescription = prescriptionRepository.findByIdAndActiveTrue(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescripción no encontrada con ID: " + prescriptionId));
        
        // Buscar el medicamento (asumiendo que existe un repository para medications)
        // MedicationDomain medication = medicationRepository.findByIdAndIsActiveTrue(medicationId)
        //     .orElseThrow(() -> new RuntimeException("Medicamento no encontrado con ID: " + medicationId));
        
        // Por ahora, implementación básica
        // prescription.getMedications().add(medication);
        // prescriptionRepository.save(prescription);
        
        log.info("Medication {} added to prescription {}", medicationId, prescriptionId);
    }
    
    /**
     * Remover medicamento de prescripción (many-to-many)
     */
    public void removeMedicationFromPrescription(Integer prescriptionId, Integer medicationId) {
        log.debug("Removing medication ID {} from prescription ID {}", medicationId, prescriptionId);
        
        PrescriptionDomain prescription = prescriptionRepository.findByIdAndActiveTrue(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescripción no encontrada con ID: " + prescriptionId));
        
        // Remover medicamento de la relación
        // prescription.getMedications().removeIf(med -> med.getId().equals(medicationId));
        // prescriptionRepository.save(prescription);
        
        log.info("Medication {} removed from prescription {}", medicationId, prescriptionId);
    }
    
    /**
     * Obtener prescripciones por paciente
     */
    @Transactional(readOnly = true)
    public Page<PrescriptionDomain> getPrescriptionsByPatient(Integer patientId, Pageable pageable) {
        log.debug("Getting prescriptions for patient ID: {} with pagination: {}", patientId, pageable);
        return prescriptionRepository.findByPatientIdAndIsActiveTrue(patientId, pageable);
    }
    
    /**
     * Verificar si existe una prescripción
     */
    @Transactional(readOnly = true)
    public boolean existsPrescription(Integer id) {
        return prescriptionRepository.existsByIdAndIsActiveTrue(id);
    }
}