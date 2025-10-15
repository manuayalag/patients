package com.fiuni.patients.mapper;

import com.fiuni.clinica.domain.patient.PrescriptionDomain;
import com.fiuni.clinica.dto.generated.PrescriptionRequest;
import com.fiuni.clinica.dto.generated.PrescriptionResponse;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversión entre PrescriptionDomain y DTOs
 * Implementa GenericMapper para operaciones básicas de mapeo
 */
@Component
@Slf4j
public class PrescriptionMapper implements GenericMapper<PrescriptionDomain, PrescriptionRequest, PrescriptionResponse> {

    @Override
    public PrescriptionDomain toEntity(PrescriptionRequest dto) {
        if (dto == null) {
            return null;
        }
        
        log.debug("Converting PrescriptionRequest to PrescriptionDomain");
        PrescriptionDomain entity = new PrescriptionDomain();
        
        // Mapear campos básicos del PrescriptionRequest
        if (dto.getPrescriptionNumber() != null) {
            entity.setPrescriptionNumber(dto.getPrescriptionNumber());
        }
        if (dto.getPrescriptionDate() != null) {
            entity.setPrescriptionDate(dto.getPrescriptionDate());
        }
        if (dto.getDoctorName() != null) {
            entity.setDoctorName(dto.getDoctorName());
        }
        if (dto.getDoctorLicense() != null) {
            entity.setDoctorLicense(dto.getDoctorLicense());
        }
        if (dto.getNotes() != null) {
            entity.setNotes(dto.getNotes());
        }
        if (dto.getValidUntil() != null) {
            entity.setValidUntil(dto.getValidUntil());
        }
        if (dto.getIsFilled() != null) {
            entity.setIsFilled(dto.getIsFilled());
        }
        
        // IMPORTANTE: Mapear el patientId - lo manejará el servicio
        // El mapper solo prepara la entidad, el servicio debe buscar y asignar el patient
        
        // Configurar valores por defecto para nueva entidad
        entity.setActive(true);
        entity.setCreatedDate(java.time.LocalDateTime.now());
        entity.setLastModified(java.time.LocalDateTime.now());
        
        return entity;
    }

    @Override
    public PrescriptionResponse toDto(PrescriptionDomain entity) {
        if (entity == null) {
            return null;
        }
        
        log.debug("Converting PrescriptionDomain to PrescriptionResponse for ID: {}", entity.getId());
        
        PrescriptionResponse dto = new PrescriptionResponse();
        
        // Mapear campos básicos
        dto.setId(entity.getId());
        dto.setActive(entity.getActive());
        dto.setVersion(entity.getVersion());
        
        // Mapear timestamps con zona horaria
        if (entity.getCreatedDate() != null) {
            dto.setCreatedDate(entity.getCreatedDate().atOffset(java.time.ZoneOffset.ofHours(-3)));
        }
        if (entity.getLastModified() != null) {
            dto.setLastModified(entity.getLastModified().atOffset(java.time.ZoneOffset.ofHours(-3)));
        }
        
        // Mapear campos específicos de prescripción
        dto.setPrescriptionNumber(entity.getPrescriptionNumber());
        dto.setPrescriptionDate(entity.getPrescriptionDate());
        dto.setDoctorName(entity.getDoctorName());
        dto.setDoctorLicense(entity.getDoctorLicense());
        dto.setNotes(entity.getNotes());
        dto.setValidUntil(entity.getValidUntil());
        dto.setIsFilled(entity.getIsFilled());
        
        // Mapear relación con paciente
        if (entity.getPatient() != null) {
            // Crear un DTO básico del paciente para evitar referencia circular
            com.fiuni.clinica.dto.generated.PatientResponse patientDto = new com.fiuni.clinica.dto.generated.PatientResponse();
            
            // Mapear solo los campos que existen en PatientDomain
            patientDto.setId(entity.getPatient().getId());
            patientDto.setFirstName(entity.getPatient().getFirstName());
            patientDto.setLastName(entity.getPatient().getLastName());
            patientDto.setDocumentNumber(entity.getPatient().getDocumentNumber());
            patientDto.setEmail(entity.getPatient().getEmail());
            patientDto.setBirthDate(entity.getPatient().getBirthDate());
            
            // Convertir enums correctamente
            if (entity.getPatient().getGender() != null) {
                patientDto.setGender(com.fiuni.clinica.dto.generated.Gender.valueOf(entity.getPatient().getGender().name()));
            }
            if (entity.getPatient().getBloodType() != null) {
                patientDto.setBloodType(com.fiuni.clinica.dto.generated.BloodType.valueOf(entity.getPatient().getBloodType().name()));
            }
            
            // Campos calculados
            patientDto.setFullName(entity.getPatient().getFirstName() + " " + entity.getPatient().getLastName());
            
            // Evitar referencia circular - no incluir prescripciones del paciente aquí
            patientDto.setPrescriptions(new java.util.ArrayList<>());
            
            dto.setPatient(patientDto);
        }
        
        // Mapear lista de medicamentos como PrescriptionMedicationResponse
        if (entity.getMedications() != null) {
            java.util.List<com.fiuni.clinica.dto.generated.PrescriptionMedicationResponse> medications = 
                entity.getMedications().stream()
                    .filter(prescriptionMed -> prescriptionMed.getActive()) // Solo medicamentos activos
                    .map(prescriptionMed -> {
                        com.fiuni.clinica.dto.generated.PrescriptionMedicationResponse prescMedDto = 
                            new com.fiuni.clinica.dto.generated.PrescriptionMedicationResponse();
                        
                        // Mapear campos básicos de PrescriptionMedication
                        prescMedDto.setId(prescriptionMed.getId());
                        prescMedDto.setDosage(prescriptionMed.getDosage());
                        prescMedDto.setFrequency(prescriptionMed.getFrequency());
                        prescMedDto.setDuration(prescriptionMed.getDuration());
                        prescMedDto.setInstructions(prescriptionMed.getInstructions());
                        prescMedDto.setQuantity(prescriptionMed.getQuantity());
                        prescMedDto.setActive(prescriptionMed.getActive());
                        
                        // Mapear información COMPLETA del medicamento
                        if (prescriptionMed.getMedication() != null) {
                            com.fiuni.clinica.dto.generated.MedicationResponse medDto = 
                                new com.fiuni.clinica.dto.generated.MedicationResponse();
                            
                            // Mapear campos básicos del medicamento
                            medDto.setId(prescriptionMed.getMedication().getId());
                            medDto.setMedicationName(prescriptionMed.getMedication().getMedicationName());
                            medDto.setGenericName(prescriptionMed.getMedication().getGenericName());
                            medDto.setMedicationType(prescriptionMed.getMedication().getMedicationType());
                            medDto.setManufacturer(prescriptionMed.getMedication().getManufacturer());
                            medDto.setDescription(prescriptionMed.getMedication().getDescription());
                            medDto.setSideEffects(prescriptionMed.getMedication().getSideEffects());
                            medDto.setContraindications(prescriptionMed.getMedication().getContraindications());
                            medDto.setActive(prescriptionMed.getMedication().getActive());
                            
                            prescMedDto.setMedication(medDto);
                        }
                        
                        return prescMedDto;
                    })
                    .collect(java.util.stream.Collectors.toList());
            dto.setMedications(medications);
        } else {
            dto.setMedications(new java.util.ArrayList<>());
        }
        
        return dto;
    }

    /**
     * Método compatible con versión anterior
     * @deprecated Use toDto instead
     */
    @Deprecated
    public PrescriptionResponse toResponse(PrescriptionDomain domain) {
        return toDto(domain);
    }

    /**
     * Convierte lista de PrescriptionDomain a lista de PrescriptionResponse
     */
    public List<PrescriptionResponse> toResponseList(List<PrescriptionDomain> domains) {
        if (domains == null) {
            return null;
        }
        
        log.debug("Converting {} PrescriptionDomains to PrescriptionResponses", domains.size());
        return domains.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateEntity(PrescriptionDomain entity, PrescriptionRequest dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        log.debug("Updating PrescriptionDomain ID: {} with new data", entity.getId());
        
        // Solo actualizar campos que no son null en el request
        if (dto.getPrescriptionNumber() != null) {
            entity.setPrescriptionNumber(dto.getPrescriptionNumber());
        }
        if (dto.getPrescriptionDate() != null) {
            entity.setPrescriptionDate(dto.getPrescriptionDate());
        }
        if (dto.getDoctorName() != null) {
            entity.setDoctorName(dto.getDoctorName());
        }
        if (dto.getDoctorLicense() != null) {
            entity.setDoctorLicense(dto.getDoctorLicense());
        }
        if (dto.getNotes() != null) {
            entity.setNotes(dto.getNotes());
        }
        if (dto.getValidUntil() != null) {
            entity.setValidUntil(dto.getValidUntil());
        }
        if (dto.getIsFilled() != null) {
            entity.setIsFilled(dto.getIsFilled());
        }
        
        // Actualizar timestamp de modificación
        entity.setLastModified(java.time.LocalDateTime.now());
        
        log.debug("Prescription {} updated successfully", entity.getId());
    }

    /**
     * Actualiza PrescriptionDomain existente con datos de PrescriptionRequest (método de compatibilidad)
     * @deprecated Use updateEntity instead
     */
    @Deprecated
    public void updateEntityFromRequest(PrescriptionDomain existing, PrescriptionRequest request) {
        updateEntity(existing, request);
    }
}