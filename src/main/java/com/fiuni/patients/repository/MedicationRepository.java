package com.fiuni.patients.repository;

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
 * Repository para MedicationDomain usando entidades del JAR externo
 */
@Repository
public interface MedicationRepository extends JpaRepository<MedicationDomain, Integer> {

    /**
     * Buscar medicamentos activos
     */
    @Query("SELECT m FROM MedicationDomain m WHERE m.active = true")
    Page<MedicationDomain> findAllActive(Pageable pageable);

    /**
     * Buscar medicamento por ID y activo
     */
    @Query("SELECT m FROM MedicationDomain m WHERE m.id = :id AND m.active = true")
    Optional<MedicationDomain> findByIdAndActiveTrue(@Param("id") Integer id);

    /**
     * Búsqueda simplificada de medicamentos por término general
     */
    @Query("SELECT m FROM MedicationDomain m WHERE m.active = true AND " +
           "(:searchTerm IS NULL OR :searchTerm = '' OR " +
           "UPPER(m.medicationName) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(m.genericName) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(m.medicationType) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(m.manufacturer) LIKE UPPER(CONCAT('%', :searchTerm, '%')) OR " +
           "UPPER(m.description) LIKE UPPER(CONCAT('%', :searchTerm, '%')))")
    List<MedicationDomain> searchMedications(@Param("searchTerm") String searchTerm);

    /**
     * Búsqueda avanzada por criterios específicos
     */
    @Query("SELECT m FROM MedicationDomain m WHERE m.active = true AND " +
           "(:name IS NULL OR :name = '' OR UPPER(m.medicationName) LIKE UPPER(CONCAT('%', :name, '%'))) AND " +
           "(:genericName IS NULL OR :genericName = '' OR UPPER(m.genericName) LIKE UPPER(CONCAT('%', :genericName, '%'))) AND " +
           "(:medicationType IS NULL OR :medicationType = '' OR UPPER(m.medicationType) LIKE UPPER(CONCAT('%', :medicationType, '%'))) AND " +
           "(:manufacturer IS NULL OR :manufacturer = '' OR UPPER(m.manufacturer) LIKE UPPER(CONCAT('%', :manufacturer, '%')))")
    List<MedicationDomain> searchMedicationsByCriteria(
            @Param("name") String name,
            @Param("genericName") String genericName,
            @Param("medicationType") String medicationType,
            @Param("manufacturer") String manufacturer);

    /**
     * Contar medicamentos activos
     */
    @Query("SELECT COUNT(m) FROM MedicationDomain m WHERE m.active = true")
    long countActiveMedications();

    /**
     * Buscar medicamentos por término general con paginación
     */
    @Query("SELECT m FROM MedicationDomain m WHERE m.active = true")
    Page<MedicationDomain> searchByTerm(@Param("searchTerm") String searchTerm, Pageable pageable);
}