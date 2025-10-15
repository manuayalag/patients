package com.fiuni.patients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Microservicio de gestión de pacientes usando JARs de Clinica Commons
 * 
 * Configuración:
 * - EntityScan: Escanea entidades del JAR externo com.fiuni.clinica.entity
 * - EnableJpaRepositories: Habilita repositorios JPA en com.fiuni.patients.repository
 * - EnableTransactionManagement: Habilita manejo de transacciones
 */
@SpringBootApplication(
    scanBasePackages = {
        "com.fiuni.patients",
        "com.fiuni.clinica.domain"  // Escanea componentes del JAR externo si es necesario
    }
)
@EntityScan(basePackages = {
    "com.fiuni.clinica.domain"           // Entidades del JAR externo (mismo patrón que ProfessionalsModule)
})
@EnableJpaRepositories(basePackages = {
    "com.fiuni.patients.repository"
})
public class PatientsMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientsMicroserviceApplication.class, args);
    }
}