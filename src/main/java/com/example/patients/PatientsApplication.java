package com.example.patients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {
    "com.example.patients",           // Entidades locales (si las hay)
    "com.fiuni.clinica.domain"        // Entidades del JAR de Clinica
})
public class PatientsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientsApplication.class, args);
    }

}
