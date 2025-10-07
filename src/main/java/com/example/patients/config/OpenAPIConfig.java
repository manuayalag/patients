package com.example.patients.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for the Patients Microservice
 * Provides comprehensive API documentation
 */
@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://patients-api.yourdomain.com")
                                .description("Production Server")
                ));
    }

    private Info apiInfo() {
        return new Info()
                .title("Patients Microservice API")
                .description("""
                    ## Patients Microservice
                    
                    REST API para la gestión de pacientes y prescripciones médicas.
                    
                    ### Funcionalidades principales:
                    - **Gestión de Pacientes**: CRUD completo con soft delete
                    - **Gestión de Prescripciones**: CRUD completo con relaciones many-to-many
                    - **Relaciones**: Pacientes ↔ Prescripciones ↔ Medicamentos
                    - **Búsquedas**: Por nombre, email, con paginación
                    - **Soft Delete**: Eliminación lógica preservando datos
                    
                    ### Arquitectura:
                    - Spring Boot 3.5.6 con Java 17
                    - PostgreSQL con Supabase
                    - JPA/Hibernate con Liquibase
                    - Arquitectura en capas con DTOs
                    - Integración con JARs de Clinica
                    
                    ### Base de datos compartida:
                    Este microservicio se conecta a la misma base de datos que otros microservicios del sistema Clinica.
                    """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Equipo de Desarrollo")
                        .email("dev@clinica.com")
                        .url("https://github.com/manuayalag/patients"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }
}