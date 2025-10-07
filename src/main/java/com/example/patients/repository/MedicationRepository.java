package com.example.patients.repository;

import com.fiuni.clinica.domain.patient.MedicationDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MedicationDomain entities
 * Provides database access methods for medications
 */
@Repository
public interface MedicationRepository extends JpaRepository<MedicationDomain, Integer> {
    
    /**
     * Find all active medications with pagination
     */
    Page<MedicationDomain> findByActiveTrue(Pageable pageable);
    
    /**
     * Find active medication by ID
     */
    Optional<MedicationDomain> findByIdAndActiveTrue(Integer id);
    
    /**
     * Find active medications by medication name containing (case insensitive)
     */
    Page<MedicationDomain> findByMedicationNameContainingIgnoreCaseAndActiveTrue(String medicationName, Pageable pageable);
    
    /**
     * Find active medications by manufacturer containing (case insensitive)
     */
    Page<MedicationDomain> findByManufacturerContainingIgnoreCaseAndActiveTrue(String manufacturer, Pageable pageable);
    
    /**
     * Find active medications by generic name containing (case insensitive)
     */
    Page<MedicationDomain> findByGenericNameContainingIgnoreCaseAndActiveTrue(String genericName, Pageable pageable);
    
    /**
     * Find active medications by medication type
     */
    Page<MedicationDomain> findByMedicationTypeAndActiveTrue(String medicationType, Pageable pageable);
    
    /**
     * Check if medication exists and is active
     */
    boolean existsByIdAndActiveTrue(Integer id);
    
    /**
     * Custom query to search medications by medication name or generic name
     */
    @Query("SELECT m FROM MedicationDomain m WHERE " +
           "(LOWER(m.medicationName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "m.active = true " +
           "ORDER BY m.id DESC")
    Page<MedicationDomain> searchByMedicationNameOrGenericName(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Count active medications
     */
    long countByActiveTrue();
    
    /**
     * Find all active medications (without pagination)
     */
    List<MedicationDomain> findByActiveTrue();
}