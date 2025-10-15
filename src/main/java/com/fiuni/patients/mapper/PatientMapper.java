package com.fiuni.patients.mapper;

import com.fiuni.clinica.domain.patient.PatientDomain;
import com.fiuni.clinica.dto.generated.PatientRequest;
import com.fiuni.clinica.dto.generated.PatientResponse;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para conversión entre PatientDomain y DTOs
 * Implementa GenericMapper para operaciones básicas de mapeo
 */
@Component
@Slf4j
public class PatientMapper implements GenericMapper<PatientDomain, PatientRequest, PatientResponse> {

    /**
     * Mapeo de BloodType domain a DTO
     * Domain: A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, AB_POSITIVE,
     * AB_NEGATIVE, O_POSITIVE, O_NEGATIVE
     * DTO: A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, AB_POSITIVE,
     * AB_NEGATIVE, O_POSITIVE, O_NEGATIVE
     */
    private com.fiuni.clinica.dto.generated.BloodType mapBloodTypeDomainToDto(
            com.fiuni.clinica.domain.enums.BloodType domainBloodType) {
        if (domainBloodType == null)
            return null;

        // Los enums ahora coinciden, mapeo directo por nombre
        return com.fiuni.clinica.dto.generated.BloodType.valueOf(domainBloodType.name());
    }

    /**
     * Mapeo de BloodType DTO a domain
     */
    private com.fiuni.clinica.domain.enums.BloodType mapBloodTypeDtoToDomain(
            com.fiuni.clinica.dto.generated.BloodType dtoBloodType) {
        if (dtoBloodType == null)
            return null;

        // Los enums ahora coinciden, mapeo directo por nombre
        return com.fiuni.clinica.domain.enums.BloodType.valueOf(dtoBloodType.name());
    }

    @Override
    public PatientDomain toEntity(PatientRequest dto) {
        if (dto == null) {
            return null;
        }

        log.debug("Converting PatientRequest to PatientDomain");
        PatientDomain entity = new PatientDomain();

        // Mapear campos básicos
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        // TEMPORAL: Campo documentType no disponible aún en PatientRequest 0.0.17, usar
        // valor por defecto
        entity.setDocumentType("CI"); // Valor por defecto para Paraguay
        entity.setDocumentNumber(dto.getDocumentNumber());
        entity.setEmail(dto.getEmail());
        entity.setBirthDate(dto.getBirthDate());
        entity.setPhone(dto.getPhone());
        entity.setAllergyNotes(dto.getAllergyNotes());
        entity.setChronicConditions(dto.getChronicConditions());

        // Mapear enums con conversión
        if (dto.getGender() != null) {
            entity.setGender(com.fiuni.clinica.domain.enums.Gender.valueOf(dto.getGender().name()));
        }

        if (dto.getBloodType() != null) {
            entity.setBloodType(mapBloodTypeDtoToDomain(dto.getBloodType()));
        }

        // Configurar valores por defecto para nueva entidad
        entity.setActive(true);
        entity.setCreatedAt(java.time.LocalDateTime.now());
        entity.setUpdatedAt(java.time.LocalDateTime.now());

        return entity;
    }

    @Override
    public PatientResponse toDto(PatientDomain entity) {
        if (entity == null) {
            return null;
        }

        log.debug("Converting PatientDomain to PatientResponse for ID: {}", entity.getId());

        PatientResponse dto = new PatientResponse();
        dto.setId(entity.getId());
        dto.setActive(entity.isActive());

        // Mapear timestamps con zona horaria
        if (entity.getCreatedAt() != null) {
            dto.setCreatedDate(entity.getCreatedAt().atOffset(ZoneOffset.ofHours(-3)));
        }
        if (entity.getUpdatedAt() != null) {
            dto.setLastModified(entity.getUpdatedAt().atOffset(ZoneOffset.ofHours(-3)));
        }

        // Mapear campos básicos
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setDocumentNumber(entity.getDocumentNumber());
        dto.setEmail(entity.getEmail());
        dto.setBirthDate(entity.getBirthDate());
        dto.setPhone(entity.getPhone());
        dto.setAllergyNotes(entity.getAllergyNotes());
        dto.setChronicConditions(entity.getChronicConditions());
        dto.setFullName(entity.getFullName() != null ? entity.getFullName()
                : entity.getFirstName() + " " + entity.getLastName());
        dto.setAge(entity.getAge());

        // Mapear enums con manejo de errores
        if (entity.getGender() != null) {
            try {
                dto.setGender(com.fiuni.clinica.dto.generated.Gender.valueOf(entity.getGender().name()));
            } catch (Exception e) {
                log.warn("Error mapping gender '{}' for patient {}: {}", entity.getGender(), entity.getId(),
                        e.getMessage());
            }
        }

        if (entity.getBloodType() != null) {
            dto.setBloodType(mapBloodTypeDomainToDto(entity.getBloodType()));
        }

        return dto;
    }

    @Override
    public void updateEntity(PatientDomain entity, PatientRequest dto) {
        if (entity == null || dto == null)
            return;

        log.debug("Updating PatientDomain ID: {} with new data", entity.getId());

        if (dto.getFirstName() != null)
            entity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null)
            entity.setLastName(dto.getLastName());
        if (dto.getDocumentNumber() != null)
            entity.setDocumentNumber(dto.getDocumentNumber());
        if (dto.getEmail() != null)
            entity.setEmail(dto.getEmail());
        if (dto.getBirthDate() != null)
            entity.setBirthDate(dto.getBirthDate());
        if (dto.getPhone() != null)
            entity.setPhone(dto.getPhone()); // 📌 Agregado
        if (dto.getAllergyNotes() != null)
            entity.setAllergyNotes(dto.getAllergyNotes()); // 📌 Agregado
        if (dto.getChronicConditions() != null)
            entity.setChronicConditions(dto.getChronicConditions()); // 📌 Agregado

        if (dto.getGender() != null) {
            try {
                entity.setGender(com.fiuni.clinica.domain.enums.Gender.valueOf(dto.getGender().name()));
            } catch (Exception e) {
                log.warn("Error mapping gender for patient {}: {}", entity.getId(), e.getMessage());
            }
        }

        if (dto.getBloodType() != null)
            entity.setBloodType(mapBloodTypeDtoToDomain(dto.getBloodType()));

        entity.setUpdatedAt(java.time.LocalDateTime.now());

        log.debug("Patient {} updated successfully", entity.getId());
    }

    /**
     * Método compatible con versión anterior
     * 
     * @deprecated Use toDto instead
     */
    @Deprecated
    public PatientResponse toResponse(PatientDomain domain) {
        return toDto(domain);
    }

    /**
     * Convierte PatientDomain a PatientResponse con prescripciones básicas (solo
     * IDs)
     */
    public PatientResponse toResponseWithPrescriptionIds(PatientDomain domain) {
        if (domain == null) {
            return null;
        }

        PatientResponse response = toResponse(domain); // Usar el mapeo básico

        // Agregar solo los IDs de las prescripciones si es necesario
        if (domain.getPrescriptions() != null && !domain.getPrescriptions().isEmpty()) {
            // Crear lista simple con solo información básica de prescripciones
            // response.setPrescriptionIds(domain.getPrescriptions().stream()
            // .map(p -> p.getId())
            // .collect(Collectors.toList()));
        }

        return response;
    }

    /**
     * Convierte lista de PatientDomain a lista de PatientResponse
     */
    public List<PatientResponse> toResponseList(List<PatientDomain> domains) {
        if (domains == null) {
            return null;
        }

        log.debug("Converting {} PatientDomains to PatientResponses", domains.size());
        return domains.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza PatientDomain existente con datos de PatientRequest (actualización
     * parcial con validación)
     * Versión que valida campos no vacíos antes de actualizar
     */
    public void updateEntityFromRequest(PatientDomain existing, PatientRequest request) {
        if (existing == null || request == null) {
            return;
        }

        log.debug("Updating PatientDomain ID: {} with partial validation", existing.getId());

        // Mapeo manual condicional - solo actualizar campos que no son null/vacíos
        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            existing.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            existing.setLastName(request.getLastName());
        }

        if (request.getDocumentNumber() != null && !request.getDocumentNumber().trim().isEmpty()) {
            existing.setDocumentNumber(request.getDocumentNumber());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            existing.setEmail(request.getEmail());
        }

        if (request.getBirthDate() != null) {
            existing.setBirthDate(request.getBirthDate());
        }

        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) { // 📌 Agregado
            existing.setPhone(request.getPhone());
        }

        if (request.getAllergyNotes() != null && !request.getAllergyNotes().trim().isEmpty()) { // 📌 Agregado
            existing.setAllergyNotes(request.getAllergyNotes());
        }

        if (request.getChronicConditions() != null && !request.getChronicConditions().trim().isEmpty()) { // 📌 Agregado
            existing.setChronicConditions(request.getChronicConditions());
        }

        // Mapear enums solo si están presentes
        if (request.getGender() != null) {
            try {
                existing.setGender(com.fiuni.clinica.domain.enums.Gender.valueOf(request.getGender().name()));
            } catch (Exception e) {
                log.warn("Error mapping gender for patient {}: {}", existing.getId(), e.getMessage());
            }
        }

        if (request.getBloodType() != null) {
            existing.setBloodType(mapBloodTypeDtoToDomain(request.getBloodType()));
        }

        // Actualizar timestamp de modificación
        existing.setUpdatedAt(java.time.LocalDateTime.now());

        log.debug("Patient {} updated successfully", existing.getId());
    }
}