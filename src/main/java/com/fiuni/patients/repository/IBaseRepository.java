package com.fiuni.patients.repository;

import com.fiuni.clinica.domain.base.BaseDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface for all domain entities.
 * Provides common CRUD operations for entities extending BaseDomain.
 */
@NoRepositoryBean
public interface IBaseRepository<E extends BaseDomain> extends JpaRepository<E, Integer> {

    /**
     * Find entity by ID (only active entities)
     * @param id Entity ID
     * @return Optional entity if found and active
     */
    Optional<E> findByIdAndIsActiveTrue(Integer id);

    /**
     * Find all active entities with pagination
     * @param pageable Pagination parameters
     * @return Page of active entities
     */
    Page<E> findByIsActiveTrue(Pageable pageable);

    /**
     * Find all active entities without pagination
     * @return List of active entities
     */
    List<E> findByIsActiveTrue();
}