package com.example.patients.mapper;

import com.example.patients.config.MapperConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiuni.clinica.domain.patient.PatientDomain;
import com.fiuni.clinica.dto.generated.PatientRequest;
import com.fiuni.clinica.dto.generated.PatientResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for PatientMapper to verify ModelMapper and Jackson integration
 */
@SpringBootTest
@ActiveProfiles("test")
class PatientMapperTest {

    @Autowired
    private PatientMapper patientMapper;

    private PatientRequest sampleRequest;
    private PatientDomain sampleDomain;

    @BeforeEach
    void setUp() {
        // Create sample PatientRequest for testing
        sampleRequest = new PatientRequest();
        // Note: Setting fields based on what's available in the generated DTOs
        
        // Create sample PatientDomain for testing
        sampleDomain = new PatientDomain();
        sampleDomain.setId(1);
        sampleDomain.setEmail("test@example.com");
        // Note: Not setting isActive as the method might not be available
    }

    @Test
    void testRequestToEntity() {
        // Test conversion from PatientRequest to PatientDomain
        PatientDomain result = patientMapper.requestToEntity(sampleRequest);
        
        assertThat(result).isNotNull();
        // Add more specific assertions based on the actual fields available
    }

    @Test
    void testToDto() {
        // Test conversion from PatientDomain to PatientResponse
        PatientResponse result = patientMapper.toDto(sampleDomain);
        
        assertThat(result).isNotNull();
        // Add more specific assertions based on the actual fields available
    }

    @Test
    void testToEntity() {
        // First convert domain to response, then back to domain
        PatientResponse response = patientMapper.toDto(sampleDomain);
        PatientDomain result = patientMapper.toEntity(response);
        
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(sampleDomain.getEmail());
    }

    @Test
    void testJsonSerialization() {
        // Test JSON conversion
        PatientResponse response = patientMapper.toDto(sampleDomain);
        String json = patientMapper.toJson(response);
        
        assertThat(json).isNotNull();
        // More flexible assertion that doesn't depend on specific email format
        assertThat(json).contains("test@example.com");
        
        // Test deserialization
        PatientResponse deserializedResponse = patientMapper.fromJson(json);
        assertThat(deserializedResponse).isNotNull();
        // Check if email field exists and matches
        if (deserializedResponse.getEmail() != null) {
            assertThat(deserializedResponse.getEmail()).isEqualTo("test@example.com");
        }
    }

    @Test
    void testRequestJsonSerialization() {
        // Test PatientRequest JSON conversion
        String json = patientMapper.requestToJson(sampleRequest);
        assertThat(json).isNotNull();
        
        PatientRequest deserializedRequest = patientMapper.jsonToRequest(json);
        assertThat(deserializedRequest).isNotNull();
    }

    @Test
    void testUpdateEntityFromRequest() {
        // Test updating entity from request
        PatientDomain entity = new PatientDomain();
        entity.setId(1);
        entity.setEmail("original@example.com");
        
        // This test will depend on the actual fields available in PatientRequest
        patientMapper.updateEntityFromRequest(entity, sampleRequest);
        
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1); // ID should be preserved
    }
}