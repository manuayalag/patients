package com.fiuni.patients.mapper;

import com.fiuni.clinica.domain.patient.MedicationDomain;
import com.fiuni.clinica.dto.generated.MedicationRequest;
import com.fiuni.clinica.dto.generated.MedicationResponse;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversión entre MedicationDomain y DTOs
 * Implementa GenericMapper para operaciones básicas de mapeo
 */
@Component
@Slf4j
public class MedicationMapper implements GenericMapper<MedicationDomain, MedicationRequest, MedicationResponse> {

    @Override
    public MedicationDomain toEntity(MedicationRequest dto) {
        if (dto == null) {
            return null;
        }
        
        log.debug("Converting MedicationRequest to MedicationDomain");
        MedicationDomain entity = new MedicationDomain();
        
        // Mapear campos reales del MedicationRequest
        entity.setMedicationName(dto.getMedicationName());
        entity.setGenericName(dto.getGenericName());
        entity.setMedicationType(dto.getMedicationType());
        entity.setManufacturer(dto.getManufacturer());
        entity.setDescription(dto.getDescription());
        entity.setSideEffects(dto.getSideEffects());
        entity.setContraindications(dto.getContraindications());
        
        // Configurar valores por defecto para nueva entidad
        entity.setIsActive(true);
        
        return entity;
    }

    @Override
    public MedicationResponse toDto(MedicationDomain entity) {
        if (entity == null) {
            return null;
        }
        
        log.debug("Converting MedicationDomain to MedicationResponse for ID: {}", entity.getId());
        
        MedicationResponse dto = new MedicationResponse();
        
        // Mapear campos básicos
        dto.setId(entity.getId());
        dto.setActive(entity.getIsActive());
        dto.setVersion(entity.getVersion());
        
        // Mapear timestamps con zona horaria
        if (entity.getCreatedDate() != null) {
            dto.setCreatedDate(entity.getCreatedDate().atOffset(ZoneOffset.ofHours(-3)));
        }
        if (entity.getLastModified() != null) {
            dto.setLastModified(entity.getLastModified().atOffset(ZoneOffset.ofHours(-3)));
        }
        
        // Mapear campos específicos de medicación
        dto.setMedicationName(entity.getMedicationName());
        dto.setGenericName(entity.getGenericName());
        dto.setMedicationType(entity.getMedicationType());
        dto.setManufacturer(entity.getManufacturer());
        dto.setDescription(entity.getDescription());
        dto.setSideEffects(entity.getSideEffects());
        dto.setContraindications(entity.getContraindications());
        
        // Calcular número de prescripciones (si existe la relación)
        if (entity.getPrescriptions() != null) {
            dto.setPrescriptionCount(entity.getPrescriptions().size());
        } else {
            dto.setPrescriptionCount(0);
        }
        
        return dto;
    }

    /**
     * Método compatible con versión anterior
     * @deprecated Use toDto instead
     */
    @Deprecated
    public MedicationResponse toResponse(MedicationDomain domain) {
        return toDto(domain);
    }

    /**
     * Convierte lista de MedicationDomain a lista de MedicationResponse
     */
    public List<MedicationResponse> toResponseList(List<MedicationDomain> domains) {
        if (domains == null) {
            return null;
        }
        
        log.debug("Converting {} MedicationDomains to MedicationResponses", domains.size());
        return domains.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateEntity(MedicationDomain entity, MedicationRequest dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        log.debug("Updating MedicationDomain ID: {} with new data", entity.getId());
        
        // Solo actualizar campos que no son null en el request
        if (dto.getMedicationName() != null) {
            entity.setMedicationName(dto.getMedicationName());
        }
        if (dto.getGenericName() != null) {
            entity.setGenericName(dto.getGenericName());
        }
        if (dto.getMedicationType() != null) {
            entity.setMedicationType(dto.getMedicationType());
        }
        if (dto.getManufacturer() != null) {
            entity.setManufacturer(dto.getManufacturer());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getSideEffects() != null) {
            entity.setSideEffects(dto.getSideEffects());
        }
        if (dto.getContraindications() != null) {
            entity.setContraindications(dto.getContraindications());
        }
        
        log.debug("Medication {} updated successfully", entity.getId());
    }

    /**
     * Actualiza MedicationDomain existente con datos de MedicationRequest (método de compatibilidad)
     * @deprecated Use updateEntity instead
     */
    @Deprecated
    public void updateEntityFromRequest(MedicationDomain existing, MedicationRequest request) {
        updateEntity(existing, request);
    }
}