package com.example.patients.service.base;

import com.example.patients.mapper.base.GenericMapper;
import com.example.patients.repository.base.IBaseRepository;
import com.fiuni.clinica.domain.base.BaseDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Abstract base service providing common CRUD operations
 * @param <E> Entity type extending BaseDomain
 * @param <D> DTO type
 */
@Slf4j
@Transactional
public abstract class AbstractBaseService<E extends BaseDomain, D> {
    
    protected final IBaseRepository<E> repository;
    protected final GenericMapper<E, D> mapper;
    
    protected AbstractBaseService(IBaseRepository<E> repository, GenericMapper<E, D> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    
    /**
     * Save entity
     */
    public D save(D dto) {
        log.debug("Saving entity: {}", dto);
        E entity = mapper.toEntity(dto);
        E savedEntity = repository.save(entity);
        return mapper.toDto(savedEntity);
    }
    
    /**
     * Find by ID 
     */
    @Transactional(readOnly = true)
    public Optional<D> findById(Integer id) {
        log.debug("Finding entity by ID: {}", id);
        return repository.findById(id)
                .map(mapper::toDto);
    }
    
    /**
     * Find by ID or throw exception
     */
    @Transactional(readOnly = true)
    public D findByIdOrThrow(Integer id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found with ID: " + id));
    }
    
    /**
     * Find all records with pagination
     */
    @Transactional(readOnly = true)
    public Page<D> findAll(Pageable pageable) {
        log.debug("Finding all entities with pagination: {}", pageable);
        return repository.findAll(pageable)
                .map(mapper::toDto);
    }
    
    /**
     * Find all records
     */
    @Transactional(readOnly = true)
    public List<D> findAll() {
        log.debug("Finding all entities");
        return mapper.toDtoList(repository.findAll());
    }
    
    /**
     * Delete by ID
     */
    public boolean deleteById(Integer id) {
        log.debug("Deleting entity with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new RuntimeException("Entity not found with ID: " + id);
        }
        
        repository.deleteById(id);
        return true;
    }
    
    /**
     * Check if entity exists
     */
    @Transactional(readOnly = true)
    public boolean exists(Integer id) {
        return repository.existsById(id);
    }
    
    /**
     * Count entities
     */
    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }
    
    /**
     * Update entity
     */
    public D update(Integer id, D dto) {
        log.debug("Updating entity with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new RuntimeException("Entity not found with ID: " + id);
        }
        
        E entity = mapper.toEntity(dto);
        E savedEntity = repository.save(entity);
        return mapper.toDto(savedEntity);
    }
}