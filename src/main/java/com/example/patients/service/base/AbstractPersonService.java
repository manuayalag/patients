package com.example.patients.service.base;

import com.example.patients.mapper.base.GenericMapper;
import com.example.patients.repository.base.IBaseRepository;
import com.fiuni.clinica.domain.base.PersonDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract service for Person entities (Patient, Professional, etc.)
 * Provides common functionality for all person-based entities
 * @param <E> Entity type extending PersonDomain
 * @param <D> DTO type
 */
@Slf4j
@Transactional
public abstract class AbstractPersonService<E extends PersonDomain, D> extends AbstractBaseService<E, D> {
    
    protected AbstractPersonService(IBaseRepository<E> repository, GenericMapper<E, D> mapper) {
        super(repository, mapper);
    }
    
    /**
     * Find persons by email (common functionality)
     */
    @Transactional(readOnly = true)
    public abstract Page<D> findByEmail(String email, Pageable pageable);
    
    /**
     * Find persons by name (common functionality)
     */
    @Transactional(readOnly = true)
    public abstract Page<D> findByName(String name, Pageable pageable);
    
    /**
     * Validate person data before save (common validation)
     */
    protected void validatePersonData(E person) {
        log.debug("Validating person data: {}", person.getDisplayName());
        
        if (!person.isValid()) {
            throw new RuntimeException("Invalid person data: " + person.getSummary());
        }
        
        if (person.getEmail() != null && !isValidEmail(person.getEmail())) {
            throw new RuntimeException("Invalid email format: " + person.getEmail());
        }
        
        if (person.getAge() < 0 || person.getAge() > 150) {
            throw new RuntimeException("Invalid age: " + person.getAge());
        }
    }
    
    /**
     * Basic email validation
     */
    protected boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * Override save to include person validation
     */
    @Override
    public D save(D dto) {
        E entity = mapper.toEntity(dto);
        validatePersonData(entity);
        return super.save(dto);
    }
    
    /**
     * Override update to include person validation  
     */
    @Override
    public D update(Integer id, D dto) {
        E entity = mapper.toEntity(dto);
        validatePersonData(entity);
        return super.update(id, dto);
    }
}