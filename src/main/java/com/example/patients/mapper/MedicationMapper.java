package com.example.patients.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fiuni.clinica.domain.patient.MedicationDomain;
import com.fiuni.clinica.dto.generated.MedicationRequest;
import com.fiuni.clinica.dto.generated.MedicationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for MedicationDomain using Jackson and ModelMapper
 * Provides automatic mapping between entities and DTOs
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MedicationMapper {
    
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    
    /**
     * Convert MedicationRequest to MedicationDomain entity
     */
    public MedicationDomain requestToEntity(MedicationRequest request) {
        log.debug("Converting MedicationRequest to MedicationDomain: {}", request);
        
        if (request == null) {
            return null;
        }
        
        MedicationDomain medication = modelMapper.map(request, MedicationDomain.class);
        
        // Set default values for new medications
        medication.setActive(true);
        medication.setCreatedDate(LocalDateTime.now());
        medication.setLastModified(LocalDateTime.now());
        
        log.debug("Converted to MedicationDomain: {}", medication);
        return medication;
    }
    
    /**
     * Convert MedicationDomain entity to MedicationResponse DTO
     */
    public MedicationResponse toDto(MedicationDomain entity) {
        log.debug("Converting MedicationDomain to MedicationResponse: {}", entity);
        
        if (entity == null) {
            return null;
        }
        
        MedicationResponse response = modelMapper.map(entity, MedicationResponse.class);
        log.debug("Converted to MedicationResponse: {}", response);
        return response;
    }
    
    /**
     * Convert MedicationRequest to MedicationDomain for updates (preserves existing ID and audit fields)
     */
    public MedicationDomain toEntity(MedicationRequest request, Integer existingId) {
        log.debug("Converting MedicationRequest to MedicationDomain with ID {}: {}", existingId, request);
        
        if (request == null) {
            return null;
        }
        
        MedicationDomain medication = modelMapper.map(request, MedicationDomain.class);
        medication.setId(existingId);
        medication.setLastModified(LocalDateTime.now());
        
        log.debug("Converted to MedicationDomain for update: {}", medication);
        return medication;
    }
    
    /**
     * Update existing MedicationDomain entity from MedicationRequest
     * Preserves audit fields and ID
     */
    public void updateEntityFromRequest(MedicationDomain entity, MedicationRequest request) {
        log.debug("Updating MedicationDomain {} from MedicationRequest: {}", entity.getId(), request);
        
        if (entity == null || request == null) {
            return;
        }
        
        // Store original audit information
        Integer originalId = entity.getId();
        LocalDateTime originalCreatedDate = entity.getCreatedDate();
        Boolean originalActive = entity.getActive();
        Integer originalVersion = entity.getVersion();
        
        // Skip null values during mapping to preserve existing data
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(request, entity);
        
        // Restore audit fields
        entity.setId(originalId);
        entity.setCreatedDate(originalCreatedDate);
        entity.setActive(originalActive);
        entity.setVersion(originalVersion);
        entity.setLastModified(LocalDateTime.now());
        
        // Reset skip null configuration
        modelMapper.getConfiguration().setSkipNullEnabled(false);
        
        log.debug("Updated MedicationDomain: {}", entity);
    }
    
    /**
     * Convert MedicationDomain to JSON string using Jackson
     */
    public String toJson(MedicationDomain entity) {
        log.debug("Converting MedicationDomain to JSON: {}", entity);
        
        try {
            String json = objectMapper.writeValueAsString(entity);
            log.debug("Converted to JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("Error converting MedicationDomain to JSON", e);
            throw new RuntimeException("Error converting to JSON", e);
        }
    }
    
    /**
     * Convert MedicationRequest to JSON string using Jackson
     */
    public String requestToJson(MedicationRequest request) {
        log.debug("Converting MedicationRequest to JSON: {}", request);
        
        try {
            String json = objectMapper.writeValueAsString(request);
            log.debug("Converted request to JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("Error converting MedicationRequest to JSON", e);
            throw new RuntimeException("Error converting to JSON", e);
        }
    }
    
    /**
     * Convert MedicationResponse to JSON string using Jackson
     */
    public String responseToJson(MedicationResponse response) {
        log.debug("Converting MedicationResponse to JSON: {}", response);
        
        try {
            String json = objectMapper.writeValueAsString(response);
            log.debug("Converted response to JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("Error converting MedicationResponse to JSON", e);
            throw new RuntimeException("Error converting to JSON", e);
        }
    }
    
    /**
     * Parse JSON string to MedicationDomain using Jackson
     */
    public MedicationDomain fromJson(String json) {
        log.debug("Converting JSON to MedicationDomain: {}", json);
        
        try {
            MedicationDomain medication = objectMapper.readValue(json, MedicationDomain.class);
            log.debug("Converted from JSON to MedicationDomain: {}", medication);
            return medication;
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to MedicationDomain", e);
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
}