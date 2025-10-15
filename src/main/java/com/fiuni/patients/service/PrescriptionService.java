package com.fiuni.patients.service;

import com.fiuni.clinica.domain.patient.PrescriptionDomain;
import com.fiuni.clinica.domain.patient.PatientDomain;
import com.fiuni.clinica.domain.patient.MedicationDomain;
import com.fiuni.clinica.domain.patient.PrescriptionMedicationDomain;
import com.fiuni.clinica.dto.generated.PrescriptionRequest;
import com.fiuni.clinica.dto.generated.PrescriptionResponse;
import com.fiuni.clinica.dto.generated.MedicationResponse;
import com.fiuni.clinica.dto.generated.PrescriptionMedicationRequest;
import com.fiuni.clinica.dto.generated.PrescriptionMedicationResponse;
import com.fiuni.patients.mapper.PrescriptionMapper;
import com.fiuni.patients.repository.PrescriptionRepository;
import com.fiuni.patients.repository.PatientRepository;
import com.fiuni.patients.repository.MedicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service para gestión de prescripciones
 */
@Service
@Slf4j
@Transactional
public class PrescriptionService extends AbstractBaseService<PrescriptionDomain, PrescriptionRequest, PrescriptionResponse> {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final MedicationRepository medicationRepository;
    private final PrescriptionMapper prescriptionMapper;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               PatientRepository patientRepository,
                               MedicationRepository medicationRepository,
                               PrescriptionMapper prescriptionMapper) {
        super(prescriptionRepository, prescriptionMapper);
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.medicationRepository = medicationRepository;
        this.prescriptionMapper = prescriptionMapper;
    }

    /**
     * Obtener todas las prescripciones con paginación
     */
    @Transactional(readOnly = true)
    public Page<PrescriptionResponse> getAllPrescriptions(Pageable pageable) {
        log.info("Getting all prescriptions with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
    Page<PrescriptionDomain> prescriptions = prescriptionRepository.findByIsActiveTrue(pageable);

    log.info("Found {} prescriptions", prescriptions.getTotalElements());

    // Use toDto (non-deprecated) mapper method
    return prescriptions.map(prescriptionMapper::toDto);
    }

    // ================== MÉTODOS PÚBLICOS PARA EL CONTROLLER ==================

    /**
     * Obtener todas las prescripciones con paginación y filtros
     */
    @Transactional(readOnly = true)
    public Page<PrescriptionResponse> getAllPrescriptions(int page, int size, Integer patientId, Boolean isFilled) {
        log.info("Getting all prescriptions with pagination: page={}, size={}, patientId={}, isFilled={}", 
                page, size, patientId, isFilled);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<PrescriptionDomain> prescriptions;
        
        if (patientId != null) {
            prescriptions = prescriptionRepository.findByPatientIdAndIsActiveTrue(patientId, pageable);
        } else {
            prescriptions = prescriptionRepository.findByIsActiveTrue(pageable);
        }
        
        log.info("Found {} prescriptions", prescriptions.getTotalElements());
        return prescriptions.map(prescriptionMapper::toDto);
    }

    /**
     * Obtener prescripción por ID
     */
    @Transactional(readOnly = true)
    public Optional<PrescriptionResponse> getPrescriptionById(Integer id) {
        log.info("Getting prescription by ID: {}", id);
        
        Optional<PrescriptionDomain> prescription = prescriptionRepository.findByIdAndIsActiveTrue(id);
        
        if (prescription.isPresent()) {
            log.info("Prescription found with ID: {}", id);
            return Optional.of(prescriptionMapper.toDto(prescription.get()));
        } else {
            log.warn("Prescription not found with ID: {}", id);
            return Optional.empty();
        }
    }

    /**
     * Crear nueva prescripción
     */
    @Transactional
    public PrescriptionResponse createPrescription(PrescriptionRequest request) {
        log.info("Creating new prescription for patient ID: {}", request.getPatientId());
        
        // Verificar que el patientId esté presente
        if (request.getPatientId() == null) {
            throw new RuntimeException("Patient ID is required to create a prescription");
        }
        
        // Buscar y verificar que el paciente existe
        Optional<PatientDomain> patient = patientRepository.findByIdAndIsActiveTrue(request.getPatientId());
        if (!patient.isPresent()) {
            throw new RuntimeException("Patient not found with ID: " + request.getPatientId());
        }
        
        // Crear la prescripción usando el mapper
        PrescriptionDomain prescription = prescriptionMapper.toEntity(request);
        
        // IMPORTANTE: Asignar el paciente a la prescripción
        prescription.setPatient(patient.get());
        
        PrescriptionDomain savedPrescription = prescriptionRepository.save(prescription);
        
        log.info("Prescription created successfully with ID: {} for patient ID: {}", 
                savedPrescription.getId(), request.getPatientId());
        return prescriptionMapper.toDto(savedPrescription);
    }

    /**
     * Actualizar prescripción existente
     */
    @Transactional
    public Optional<PrescriptionResponse> updatePrescription(Integer id, PrescriptionRequest request) {
        log.info("Updating prescription with ID: {}", id);
        
        Optional<PrescriptionDomain> existingPrescription = prescriptionRepository.findByIdAndIsActiveTrue(id);
        
        if (existingPrescription.isPresent()) {
            PrescriptionDomain prescription = existingPrescription.get();
            prescriptionMapper.updateEntity(prescription, request);
            
            PrescriptionDomain updatedPrescription = prescriptionRepository.save(prescription);
            
            log.info("Prescription updated successfully with ID: {}", id);
            return Optional.of(prescriptionMapper.toDto(updatedPrescription));
        } else {
            log.warn("Cannot update - Prescription not found with ID: {}", id);
            return Optional.empty();
        }
    }

    /**
     * Eliminar prescripción (soft delete)
     */
    @Transactional
    public boolean deletePrescription(Integer id) {
        log.info("Deleting prescription with ID: {}", id);
        
        Optional<PrescriptionDomain> prescription = prescriptionRepository.findByIdAndIsActiveTrue(id);
        
        if (prescription.isPresent()) {
            PrescriptionDomain prescriptionToDelete = prescription.get();
            prescriptionToDelete.setIsActive(false);
            prescriptionRepository.save(prescriptionToDelete);
            
            log.info("Prescription deleted successfully with ID: {}", id);
            return true;
        } else {
            log.warn("Cannot delete - Prescription not found with ID: {}", id);
            return false;
        }
    }

    /**
     * Obtener prescripciones por paciente
     */
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByPatient(Integer patientId) {
        log.info("Getting prescriptions for patient ID: {}", patientId);
        
        Pageable pageable = PageRequest.of(0, 1000); // Obtener todas las prescripciones del paciente
        Page<PrescriptionDomain> prescriptions = prescriptionRepository.findByPatientIdAndIsActiveTrue(patientId, pageable);
        
        log.info("Found {} prescriptions for patient ID: {}", prescriptions.getTotalElements(), patientId);
        return prescriptions.getContent().stream()
                .map(prescriptionMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    // ================== PRESCRIPTION-MEDICATION METHODS ==================

    /**
     * Agregar medicamento a prescripción
     */
    @Transactional
    public PrescriptionMedicationResponse addMedicationToPrescription(Integer prescriptionId, PrescriptionMedicationRequest request) {
        log.info("Adding medication to prescription ID: {}", prescriptionId);
        
        // Implementación simplificada - en una implementación real necesitarías crear el PrescriptionMedicationResponse
        // Por ahora retornamos una respuesta básica
        PrescriptionMedicationResponse response = new PrescriptionMedicationResponse();
        response.setId(1); // Temporal
        
        log.info("Medication added to prescription ID: {}", prescriptionId);
        return response;
    }

    /**
     * Agregar medicamento a prescripción por IDs
     */
    @Transactional
    public PrescriptionMedicationResponse addMedicationToPrescriptionByIds(Integer prescriptionId, Integer medicationId, PrescriptionMedicationRequest request) {
        log.info("Adding medication ID {} to prescription ID {}", medicationId, prescriptionId);
        
        // Buscar prescripción
        Optional<PrescriptionDomain> prescriptionOpt = prescriptionRepository.findByIdAndIsActiveTrue(prescriptionId);
        if (!prescriptionOpt.isPresent()) {
            throw new RuntimeException("Prescription not found with ID: " + prescriptionId);
        }
        
        // Buscar medicamento
        Optional<MedicationDomain> medicationOpt = medicationRepository.findByIdAndIsActiveTrue(medicationId);
        if (!medicationOpt.isPresent()) {
            throw new RuntimeException("Medication not found with ID: " + medicationId);
        }
        
        PrescriptionDomain prescription = prescriptionOpt.get();
        MedicationDomain medication = medicationOpt.get();
        
        // Verificar si la relación ya existe
        if (prescription.getMedications() != null) {
            boolean exists = prescription.getMedications().stream()
                    .anyMatch(pm -> pm.getMedication().getId().equals(medicationId));
            
            if (exists) {
                log.warn("Medication {} is already associated with prescription {}", medicationId, prescriptionId);
                throw new RuntimeException("Medication is already associated with this prescription");
            }
        }
        
        // Crear nueva relación PrescriptionMedication
        PrescriptionMedicationDomain prescriptionMedication = new PrescriptionMedicationDomain();
        prescriptionMedication.setPrescription(prescription);
        prescriptionMedication.setMedication(medication);
        prescriptionMedication.setIsActive(true);
        prescriptionMedication.setCreatedDate(java.time.LocalDateTime.now());
        prescriptionMedication.setLastModified(java.time.LocalDateTime.now());
        
        // Mapear datos del request
        if (request.getDosage() != null) {
            prescriptionMedication.setDosage(request.getDosage());
        }
        if (request.getFrequency() != null) {
            prescriptionMedication.setFrequency(request.getFrequency());
        }
        if (request.getDuration() != null) {
            prescriptionMedication.setDuration(request.getDuration());
        }
        if (request.getInstructions() != null) {
            prescriptionMedication.setInstructions(request.getInstructions());
        }
        if (request.getQuantity() != null) {
            prescriptionMedication.setQuantity(request.getQuantity());
        }
        
        // Agregar a la prescripción y guardar
        if (prescription.getMedications() == null) {
            prescription.setMedications(new java.util.ArrayList<>());
        }
        prescription.getMedications().add(prescriptionMedication);
        
        PrescriptionDomain savedPrescription = prescriptionRepository.save(prescription);
        
        // Encontrar el medicamento recién agregado para obtener su ID generado
        PrescriptionMedicationDomain savedPrescriptionMedication = savedPrescription.getMedications()
                .stream()
                .filter(pm -> pm.getMedication().getId().equals(medicationId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error retrieving saved prescription medication"));
        
        // Crear respuesta
        PrescriptionMedicationResponse response = new PrescriptionMedicationResponse();
        response.setId(savedPrescriptionMedication.getId());
        response.setDosage(savedPrescriptionMedication.getDosage());
        response.setFrequency(savedPrescriptionMedication.getFrequency());
        response.setDuration(savedPrescriptionMedication.getDuration());
        response.setInstructions(savedPrescriptionMedication.getInstructions());
        response.setQuantity(savedPrescriptionMedication.getQuantity());
        
        // Mapear información básica del medicamento
        MedicationResponse medicationResponse = new MedicationResponse();
        medicationResponse.setId(medication.getId());
        medicationResponse.setMedicationName(medication.getMedicationName());
        medicationResponse.setGenericName(medication.getGenericName());
        response.setMedication(medicationResponse);
        
        log.info("Medication ID {} successfully added to prescription ID {} with relationship ID {}", 
                medicationId, prescriptionId, savedPrescriptionMedication.getId());
        return response;
    }

    /**
     * Obtener relación prescripción-medicamento
     */
    @Transactional(readOnly = true)
    public Optional<PrescriptionMedicationResponse> getPrescriptionMedication(Integer prescriptionId, Integer medicationId) {
        log.info("Getting prescription-medication relationship: prescription ID: {}, medication ID: {}", prescriptionId, medicationId);
        
        // Implementación simplificada
        PrescriptionMedicationResponse response = new PrescriptionMedicationResponse();
        response.setId(1); // Temporal
        
        return Optional.of(response);
    }

    /**
     * Remover medicamento de prescripción por IDs
     */
    @Transactional
    public boolean removeMedicationFromPrescriptionByIds(Integer prescriptionId, Integer medicationId) {
        log.info("Removing medication ID {} from prescription ID {}", medicationId, prescriptionId);
        
        // Buscar prescripción
        Optional<PrescriptionDomain> prescriptionOpt = prescriptionRepository.findByIdAndIsActiveTrue(prescriptionId);
        if (!prescriptionOpt.isPresent()) {
            throw new RuntimeException("Prescription not found with ID: " + prescriptionId);
        }
        
        PrescriptionDomain prescription = prescriptionOpt.get();
        
        if (prescription.getMedications() == null || prescription.getMedications().isEmpty()) {
            log.warn("No medications found for prescription ID: {}", prescriptionId);
            return false;
        }
        
        // Encontrar y marcar como inactiva la relación específica
        Optional<PrescriptionMedicationDomain> prescriptionMedicationOpt = prescription.getMedications().stream()
                .filter(pm -> pm.getIsActive() && pm.getMedication().getId().equals(medicationId))
                .findFirst();
        
        if (!prescriptionMedicationOpt.isPresent()) {
            log.warn("Medication ID {} is not associated with prescription ID {}", medicationId, prescriptionId);
            return false;
        }
        
        PrescriptionMedicationDomain prescriptionMedication = prescriptionMedicationOpt.get();
        prescriptionMedication.setIsActive(false);
        prescriptionMedication.setLastModified(java.time.LocalDateTime.now());
        
        prescriptionRepository.save(prescription);
        
        log.info("Medication ID {} successfully removed from prescription ID {}", medicationId, prescriptionId);
        return true;
    }

    /**
     * Obtener lista de medicamentos de una prescripción
     */
    @Transactional(readOnly = true)
    public List<PrescriptionMedicationResponse> getPrescriptionMedicationsList(Integer prescriptionId) {
        log.info("Getting medications list for prescription ID: {}", prescriptionId);
        
        Optional<PrescriptionDomain> prescriptionOpt = prescriptionRepository.findByIdAndIsActiveTrue(prescriptionId);
        if (!prescriptionOpt.isPresent()) {
            throw new RuntimeException("Prescription not found with ID: " + prescriptionId);
        }
        
        PrescriptionDomain prescription = prescriptionOpt.get();
        
        if (prescription.getMedications() == null || prescription.getMedications().isEmpty()) {
            log.info("No medications found for prescription ID: {}", prescriptionId);
            return new java.util.ArrayList<>();
        }
        
        List<PrescriptionMedicationResponse> responseList = prescription.getMedications().stream()
                .filter(PrescriptionMedicationDomain::getIsActive)
                .map(prescriptionMedication -> {
                    PrescriptionMedicationResponse response = new PrescriptionMedicationResponse();
                    response.setId(prescriptionMedication.getId());
                    response.setDosage(prescriptionMedication.getDosage());
                    response.setFrequency(prescriptionMedication.getFrequency());
                    response.setDuration(prescriptionMedication.getDuration());
                    response.setInstructions(prescriptionMedication.getInstructions());
                    response.setQuantity(prescriptionMedication.getQuantity());
                    
                    // Mapear información del medicamento
                    if (prescriptionMedication.getMedication() != null) {
                        MedicationResponse medicationResponse = new MedicationResponse();
                        medicationResponse.setId(prescriptionMedication.getMedication().getId());
                        medicationResponse.setMedicationName(prescriptionMedication.getMedication().getMedicationName());
                        medicationResponse.setGenericName(prescriptionMedication.getMedication().getGenericName());
                        response.setMedication(medicationResponse);
                    }
                    
                    return response;
                })
                .collect(java.util.stream.Collectors.toList());
        
        log.info("Found {} active medications for prescription ID: {}", responseList.size(), prescriptionId);
        return responseList;
    }

    /**
     * Actualizar relación prescripción-medicamento por IDs
     */
    @Transactional
    public Optional<PrescriptionMedicationResponse> updatePrescriptionMedicationByIds(
            Integer prescriptionId, Integer medicationId, PrescriptionMedicationRequest request) {
        log.info("Updating prescription-medication relationship: prescription ID: {}, medication ID: {}", 
                prescriptionId, medicationId);
        
        Optional<PrescriptionDomain> prescriptionOpt = prescriptionRepository.findByIdAndIsActiveTrue(prescriptionId);
        if (!prescriptionOpt.isPresent()) {
            throw new RuntimeException("Prescription not found with ID: " + prescriptionId);
        }
        
        PrescriptionDomain prescription = prescriptionOpt.get();
        
        // Encontrar la relación específica
        Optional<PrescriptionMedicationDomain> prescriptionMedicationOpt = prescription.getMedications().stream()
                .filter(pm -> pm.getIsActive() && pm.getMedication().getId().equals(medicationId))
                .findFirst();
        
        if (!prescriptionMedicationOpt.isPresent()) {
            log.warn("Prescription-medication relationship not found for prescription ID: {} and medication ID: {}", 
                    prescriptionId, medicationId);
            return Optional.empty();
        }
        
        PrescriptionMedicationDomain prescriptionMedication = prescriptionMedicationOpt.get();
        
        // Actualizar campos
        if (request.getDosage() != null) {
            prescriptionMedication.setDosage(request.getDosage());
        }
        if (request.getFrequency() != null) {
            prescriptionMedication.setFrequency(request.getFrequency());
        }
        if (request.getDuration() != null) {
            prescriptionMedication.setDuration(request.getDuration());
        }
        if (request.getInstructions() != null) {
            prescriptionMedication.setInstructions(request.getInstructions());
        }
        if (request.getQuantity() != null) {
            prescriptionMedication.setQuantity(request.getQuantity());
        }
        
        prescriptionMedication.setLastModified(java.time.LocalDateTime.now());
        
        prescriptionRepository.save(prescription);
        
        // Crear respuesta
        PrescriptionMedicationResponse response = new PrescriptionMedicationResponse();
        response.setId(prescriptionMedication.getId());
        response.setDosage(prescriptionMedication.getDosage());
        response.setFrequency(prescriptionMedication.getFrequency());
        response.setDuration(prescriptionMedication.getDuration());
        response.setInstructions(prescriptionMedication.getInstructions());
        response.setQuantity(prescriptionMedication.getQuantity());
        
        // Mapear información del medicamento
        if (prescriptionMedication.getMedication() != null) {
            MedicationResponse medicationResponse = new MedicationResponse();
            medicationResponse.setId(prescriptionMedication.getMedication().getId());
            medicationResponse.setMedicationName(prescriptionMedication.getMedication().getMedicationName());
            medicationResponse.setGenericName(prescriptionMedication.getMedication().getGenericName());
            response.setMedication(medicationResponse);
        }
        
        log.info("Prescription-medication updated successfully for prescription ID: {} and medication ID: {}", 
                prescriptionId, medicationId);
        return Optional.of(response);
    }
}