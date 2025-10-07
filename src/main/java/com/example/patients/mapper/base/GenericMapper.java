package com.example.patients.mapper.base;

import java.util.List;

/**
 * Generic mapper interface for converting between Entity and DTO
 * @param <E> Entity type
 * @param <D> DTO type
 */
public interface GenericMapper<E, D> {
    
    /**
     * Convert DTO to Entity
     */
    E toEntity(D dto);
    
    /**
     * Convert Entity to DTO
     */
    D toDto(E entity);
    
    /**
     * Convert list of DTOs to list of Entities
     */
    List<E> toEntityList(List<D> dtoList);
    
    /**
     * Convert list of Entities to list of DTOs
     */
    List<D> toDtoList(List<E> entityList);
}