package com.example.patients.mapper;

import com.example.patients.mapper.base.GenericMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiuni.clinica.domain.patient.PatientDomain;
import com.fiuni.clinica.dto.generated.PatientRequest;
import com.fiuni.clinica.dto.generated.PatientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between PatientDomain and PatientRequest/PatientResponse
 * Uses ModelMapper for object mapping and Jackson for JSON serialization
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PatientMapper implements GenericMapper<PatientDomain, PatientResponse> {
    
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    
    /**
     * Convert PatientRequest to PatientDomain
     * Uses ModelMapper for automatic field mapping
     */
    public PatientDomain requestToEntity(PatientRequest request) {
        try {
            log.debug("Converting PatientRequest to PatientDomain: {}", request);
            PatientDomain domain = modelMapper.map(request, PatientDomain.class);
            log.debug("Successfully converted to PatientDomain: {}", domain);
            return domain;
        } catch (Exception e) {
            log.error("Error converting PatientRequest to PatientDomain: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert PatientRequest to PatientDomain", e);
        }
    }
    
    /**
     * Convert PatientDomain to PatientResponse
     * Uses ModelMapper for automatic field mapping
     */
    @Override
    public PatientResponse toDto(PatientDomain entity) {
        try {
            log.debug("Converting PatientDomain to PatientResponse: {}", entity);
            PatientResponse response = modelMapper.map(entity, PatientResponse.class);
            log.debug("Successfully converted to PatientResponse: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error converting PatientDomain to PatientResponse: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert PatientDomain to PatientResponse", e);
        }
    }
    
    /**
     * Convert PatientResponse to PatientDomain
     * Uses ModelMapper for automatic field mapping
     */
    @Override
    public PatientDomain toEntity(PatientResponse dto) {
        try {
            log.debug("Converting PatientResponse to PatientDomain: {}", dto);
            PatientDomain domain = modelMapper.map(dto, PatientDomain.class);
            log.debug("Successfully converted to PatientDomain: {}", domain);
            return domain;
        } catch (Exception e) {
            log.error("Error converting PatientResponse to PatientDomain: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert PatientResponse to PatientDomain", e);
        }
    }
    
    /**
     * Convert list of PatientDomain to list of PatientResponse
     */
    @Override
    public List<PatientResponse> toDtoList(List<PatientDomain> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of PatientResponse to list of PatientDomain
     */
    @Override
    public List<PatientDomain> toEntityList(List<PatientResponse> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert PatientResponse to JSON string
     * Uses Jackson ObjectMapper for serialization
     */
    public String toJson(PatientResponse response) {
        try {
            log.debug("Converting PatientResponse to JSON: {}", response);
            String json = objectMapper.writeValueAsString(response);
            log.debug("Successfully converted to JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("Error converting PatientResponse to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert PatientResponse to JSON", e);
        }
    }
    
    /**
     * Convert JSON string to PatientResponse
     * Uses Jackson ObjectMapper for deserialization
     */
    public PatientResponse fromJson(String json) {
        try {
            log.debug("Converting JSON to PatientResponse: {}", json);
            PatientResponse response = objectMapper.readValue(json, PatientResponse.class);
            log.debug("Successfully converted from JSON: {}", response);
            return response;
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to PatientResponse: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert JSON to PatientResponse", e);
        }
    }
    
    /**
     * Convert PatientRequest to JSON string
     * Uses Jackson ObjectMapper for serialization
     */
    public String requestToJson(PatientRequest request) {
        try {
            log.debug("Converting PatientRequest to JSON: {}", request);
            String json = objectMapper.writeValueAsString(request);
            log.debug("Successfully converted to JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("Error converting PatientRequest to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert PatientRequest to JSON", e);
        }
    }
    
    /**
     * Convert JSON string to PatientRequest
     * Uses Jackson ObjectMapper for deserialization
     */
    public PatientRequest jsonToRequest(String json) {
        try {
            log.debug("Converting JSON to PatientRequest: {}", json);
            PatientRequest request = objectMapper.readValue(json, PatientRequest.class);
            log.debug("Successfully converted from JSON: {}", request);
            return request;
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to PatientRequest: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert JSON to PatientRequest", e);
        }
    }
    
    /**
     * Update existing PatientDomain with data from PatientRequest
     * Uses ModelMapper to merge non-null fields
     */
    public void updateEntityFromRequest(PatientDomain entity, PatientRequest request) {
        try {
            log.debug("Updating PatientDomain {} with PatientRequest {}", entity, request);
            modelMapper.map(request, entity);
            log.debug("Successfully updated PatientDomain: {}", entity);
        } catch (Exception e) {
            log.error("Error updating PatientDomain from PatientRequest: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update PatientDomain from PatientRequest", e);
        }
    }
}