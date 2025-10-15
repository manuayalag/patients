package com.fiuni.patients.mapper;

/**
 * Interfaz genérica para mappers que define operaciones básicas de conversión
 * Entre entidades de dominio y DTOs de request/response
 * 
 * @param <E> Entidad de dominio
 * @param <Req> DTO de request
 * @param <Res> DTO de response
 */
public interface GenericMapper<E, Req, Res> {
    
    /**
     * Convierte request DTO a entidad de dominio
     * @param dto DTO de request
     * @return Entidad de dominio
     */
    E toEntity(Req dto);

    /**
     * Convierte entidad de dominio a response DTO
     * @param entity Entidad de dominio
     * @return DTO de response
     */
    Res toDto(E entity);

    /**
     * Actualiza entidad existente con datos del request DTO
     * Usado para operaciones de actualización (PUT/PATCH)
     * @param entity Entidad existente a actualizar
     * @param dto DTO con nuevos datos
     */
    void updateEntity(E entity, Req dto);
}