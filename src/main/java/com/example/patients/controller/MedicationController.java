package com.example.patients.controller;

import com.fiuni.clinica.domain.patient.MedicationDomain;
import com.fiuni.clinica.dto.generated.MedicationRequest;
import com.fiuni.clinica.dto.generated.MedicationResponse;
import com.example.patients.dto.PaginationRequest;
import com.example.patients.mapper.MedicationMapper;
import com.example.patients.service.MedicationService;
import com.example.patients.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

/**
 * REST Controller for Medication operations
 * Provides comprehensive CRUD operations and search functionality
 */
@RestController
@RequestMapping("/api/v1/medications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Medications", description = "Medication management operations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MedicationController {
    
    private final MedicationService medicationService;
    private final MedicationMapper medicationMapper;
    
    /**
     * Get all medications with pagination
     */
    @GetMapping
    @Operation(summary = "Get all medications", 
               description = "Retrieves all active medications with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Medications retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<MedicationResponse>> getAllMedications(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sort) {
        
        log.info("GET /api/v1/medications - page: {}, size: {}, sort: {}", page, size, sort);
        
        Pageable pageable = PaginationUtil.toPageable(new PaginationRequest(page, size, List.of(sort + ",asc")));
        Page<MedicationDomain> medicationsPage = medicationService.getAllMedications(pageable);
        
        // Map entities to responses
        Page<MedicationResponse> responsePage = medicationsPage.map(medicationMapper::toDto);
        
        log.info("Retrieved {} medications from {} total", 
                responsePage.getNumberOfElements(), responsePage.getTotalElements());
        
        return ResponseEntity.ok(responsePage);
    }
    
    /**
     * Search medications with pagination using POST body (recommended approach)
     */
    @PostMapping("/search")
    @Operation(summary = "Search medications with pagination", 
               description = "Search medications by name or laboratory with pagination parameters in request body")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<MedicationResponse>> searchMedications(
            @Valid @RequestBody PaginationRequest paginationRequest,
            @Parameter(description = "Name to search for") @RequestParam(required = false) String name,
            @Parameter(description = "Laboratory to search for") @RequestParam(required = false) String laboratory) {
        
        log.info("POST /api/v1/medications/search - pagination: {}, name: {}, laboratory: {}", 
                paginationRequest, name, laboratory);
        
        Pageable pageable = PaginationUtil.toPageable(paginationRequest);
        Page<MedicationDomain> medicationsPage;
        
        if (name != null && !name.trim().isEmpty()) {
            medicationsPage = medicationService.searchMedicationsByName(name.trim(), pageable);
        } else if (laboratory != null && !laboratory.trim().isEmpty()) {
            medicationsPage = medicationService.searchMedicationsByManufacturer(laboratory.trim(), pageable);
        } else {
            // No search criteria, return all active medications
            medicationsPage = medicationService.getAllMedications(pageable);
        }
        
        // Map entities to responses
        Page<MedicationResponse> responsePage = medicationsPage.map(medicationMapper::toDto);
        
        log.info("Search returned {} medications from {} total", 
                responsePage.getNumberOfElements(), responsePage.getTotalElements());
        
        return ResponseEntity.ok(responsePage);
    }
    
    /**
     * Get medication by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get medication by ID", 
               description = "Retrieve a specific medication by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Medication found",
                    content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
        @ApiResponse(responseCode = "404", description = "Medication not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MedicationResponse> getMedicationById(
            @Parameter(description = "Medication ID", required = true) @PathVariable Integer id) {
        
        log.info("GET /api/v1/medications/{}", id);
        
        Optional<MedicationDomain> medicationOpt = medicationService.getMedicationById(id);
        
        if (medicationOpt.isPresent()) {
            MedicationResponse response = medicationMapper.toDto(medicationOpt.get());
            log.info("Medication found with ID: {}", id);
            return ResponseEntity.ok(response);
        } else {
            log.warn("Medication not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    

    
    /**
     * Create new medication
     */
    @PostMapping
    @Operation(summary = "Create new medication", 
               description = "Create a new medication using MedicationRequest")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Medication created successfully",
                    content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid medication data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MedicationResponse> createMedication(
            @Valid @RequestBody MedicationRequest medicationRequest) {
        
        log.info("POST /api/v1/medications - Creating medication: {}", 
                medicationMapper.requestToJson(medicationRequest));
        
        MedicationResponse createdMedication = medicationService.createMedication(medicationRequest);
        
        log.info("Medication created successfully with ID: {}", createdMedication.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMedication);
    }
    
    /**
     * Update existing medication
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update medication", 
               description = "Update an existing medication using MedicationRequest")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Medication updated successfully",
                    content = @Content(schema = @Schema(implementation = MedicationResponse.class))),
        @ApiResponse(responseCode = "404", description = "Medication not found"),
        @ApiResponse(responseCode = "400", description = "Invalid medication data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MedicationResponse> updateMedication(
            @Parameter(description = "Medication ID", required = true) @PathVariable Integer id,
            @Valid @RequestBody MedicationRequest medicationRequest) {
        
        log.info("PUT /api/v1/medications/{} - Updating medication: {}", 
                id, medicationMapper.requestToJson(medicationRequest));
        
        try {
            MedicationResponse updatedMedication = medicationService.updateMedication(id, medicationRequest);
            log.info("Medication updated successfully with ID: {}", id);
            return ResponseEntity.ok(updatedMedication);
        } catch (RuntimeException e) {
            log.warn("Medication not found for update with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete medication (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete medication", 
               description = "Soft delete a medication (sets isActive to false)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Medication deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Medication not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteMedication(
            @Parameter(description = "Medication ID", required = true) @PathVariable Integer id) {
        
        log.info("DELETE /api/v1/medications/{}", id);
        
        try {
            medicationService.deleteMedication(id);
            log.info("Medication soft deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Medication not found for deletion with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Check if medication exists
     */
    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if medication exists", 
               description = "Check if a medication exists and is active")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> existsById(
            @Parameter(description = "Medication ID", required = true) @PathVariable Integer id) {
        
        log.info("GET /api/v1/medications/{}/exists", id);
        
        boolean exists = medicationService.existsById(id);
        log.info("Medication existence check for ID {}: {}", id, exists);
        
        return ResponseEntity.ok(exists);
    }
}