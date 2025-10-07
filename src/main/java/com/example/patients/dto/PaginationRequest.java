package com.example.patients.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for pagination parameters in request body
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Parámetros de paginación y ordenamiento")
public class PaginationRequest {
    
    @Schema(description = "Número de página (basado en 0)", example = "0", defaultValue = "0")
    @Min(value = 0, message = "El número de página debe ser mayor o igual a 0")
    @NotNull
    private Integer page = 0;
    
    @Schema(description = "Tamaño de página", example = "10", defaultValue = "20")
    @Min(value = 1, message = "El tamaño de página debe ser mayor a 0")
    @NotNull
    private Integer size = 20;
    
    @Schema(description = "Campos de ordenamiento", 
            example = "[\"id,asc\", \"email,desc\"]",
            defaultValue = "[]")
    private List<String> sort;
    
    /**
     * Constructor with page and size only
     */
    public PaginationRequest(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }
}