package com.example.patients.controller;

import com.example.patients.dto.PaginationRequest;
import com.example.patients.service.PatientService;
import com.example.patients.util.PaginationUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;

// Importar las clases del JAR de Clinica
import com.fiuni.clinica.domain.patient.PatientDomain;
import com.fiuni.clinica.domain.patient.PrescriptionDomain;

// Swagger imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Patients", description = "API para la gestión de pacientes")
@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    @Operation(summary = "Obtener todos los pacientes", 
                   description = "Retorna una lista paginada de todos los pacientes activos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pacientes obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<Page<PatientDomain>> getAllPatients(
            @Parameter(description = "Configuración de paginación") 
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Getting all patients with pagination: {}", pageable);
        
        try {
            Page<PatientDomain> patients = patientService.getAllPatients(pageable);
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            log.error("Error getting all patients", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener todos los pacientes (con paginación en body)", 
               description = "Retorna una lista paginada de todos los pacientes activos. Los parámetros de paginación se envían en el body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pacientes obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/search")
    public ResponseEntity<Page<PatientDomain>> getAllPatientsWithPagination(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Parámetros de paginación y ordenamiento",
                content = @Content(
                    schema = @Schema(implementation = PaginationRequest.class),
                    examples = @ExampleObject(
                        name = "Ejemplo de paginación",
                        value = """
                        {
                          "page": 0,
                          "size": 10,
                          "sort": ["id,asc", "email,desc"]
                        }
                        """
                    )
                )
            )
            @Valid @RequestBody PaginationRequest paginationRequest) {
        
        log.info("Getting all patients with pagination request: {}", paginationRequest);
        
        try {
            Pageable pageable = PaginationUtil.toPageable(paginationRequest);
            Page<PatientDomain> patients = patientService.getAllPatients(pageable);
            
            log.info("Found {} patients, page {} of {}", 
                    patients.getNumberOfElements(), 
                    patients.getNumber(), 
                    patients.getTotalPages());
            
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            log.error("Error getting all patients with pagination request: {}", paginationRequest, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDomain> getPatientById(@PathVariable Integer id) {
        log.info("Getting patient by ID: {}", id);
        
        try {
            return patientService.getPatientById(id)
                    .map(patient -> ResponseEntity.ok(patient))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting patient by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Crear nuevo paciente", 
               description = """
                   Crea un nuevo paciente en el sistema.
                   
                   Ejemplo de JSON para el body:
                   {
                     "firstName": "Ana",
                     "lastName": "Silva", 
                     "documentType": "DNI",
                     "documentNumber": "1234567",
                     "gender": "FEMALE",
                     "email": "ana.silva@email.com",
                     "phone": "+595981987654",
                     "address": "Av. España 890, Asunción",
                     "birthDate": "1988-12-05",
                     "bloodType": "AB_POS",
                     "isActive": true
                   }
                   """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Paciente creado exitosamente",
                    content = @Content(schema = @Schema(implementation = PatientDomain.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<PatientDomain> createPatient(
            @Parameter(description = "JSON con los datos del paciente a crear (ver ejemplo en la descripción del endpoint)")
            @Valid @RequestBody PatientDomain patientData) {
        log.info("Creating new patient with email: {}", patientData.getEmail());
        
        try {
            PatientDomain createdPatient = patientService.createPatient(patientData);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
        } catch (RuntimeException e) {
            log.error("Error creating patient: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error creating patient", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientDomain> updatePatient(
            @PathVariable Integer id, 
            @Valid @RequestBody PatientDomain patientData) {
        log.info("Updating patient with ID: {}", id);
        
        try {
            PatientDomain updatedPatient = patientService.updatePatient(id, patientData);
            return ResponseEntity.ok(updatedPatient);
        } catch (RuntimeException e) {
            log.error("Error updating patient: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating patient with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Integer id) {
        log.info("Deleting patient with ID: {}", id);
        
        try {
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting patient: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting patient with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoints específicos para el microservicio de pacientes
    @GetMapping("/{patientId}/prescriptions")
    public ResponseEntity<List<PrescriptionDomain>> getPatientPrescriptions(@PathVariable Integer patientId) {
        log.info("Getting prescriptions for patient ID: {}", patientId);
        
        try {
            List<PrescriptionDomain> prescriptions = patientService.getPatientPrescriptions(patientId);
            return ResponseEntity.ok(prescriptions);
        } catch (RuntimeException e) {
            log.error("Error getting prescriptions: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting prescriptions for patient ID: {}", patientId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PatientDomain>> searchPatientsByName(
            @RequestParam String name,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("Searching patients by name: {}", name);
        
        try {
            Page<PatientDomain> patients = patientService.searchPatientsByName(name, pageable);
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            log.error("Error searching patients by name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PatientDomain> getPatientByEmail(@PathVariable String email) {
        log.info("Getting patient by email: {}", email);
        
        try {
            return patientService.getPatientByEmail(email)
                    .map(patient -> ResponseEntity.ok(patient))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting patient by email: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}