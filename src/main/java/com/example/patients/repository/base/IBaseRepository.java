package com.example.patients.repository.base;

import com.fiuni.clinica.domain.base.BaseDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface for all domain entities
 * Provides basic CRUD operations
 */
@NoRepositoryBean
public interface IBaseRepository<E extends BaseDomain> extends JpaRepository<E, Integer> {
    
    /**
     * Find all records with pagination
     */
    Page<E> findAll(Pageable pageable);
}