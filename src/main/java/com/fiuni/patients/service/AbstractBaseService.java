package com.fiuni.patients.service;

import com.fiuni.clinica.domain.base.BaseDomain;
import com.fiuni.patients.mapper.GenericMapper;
import com.fiuni.patients.repository.IBaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Generic base service that provides common CRUD operations for entities
 * E - domain entity extending BaseDomain
 * Req - request DTO
 * Res - response DTO
 */
public abstract class AbstractBaseService<E extends BaseDomain, Req, Res> {

    protected final IBaseRepository<E> repository;
    protected final GenericMapper<E, Req, Res> mapper;

    protected AbstractBaseService(IBaseRepository<E> repository, GenericMapper<E, Req, Res> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<Res> getAll(Pageable pageable) {
        return repository.findByIsActiveTrue(pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<Res> getById(Integer id) {
        return repository.findByIdAndIsActiveTrue(id).map(mapper::toDto);
    }

    @Transactional
    public Res create(Req request) {
        E entity = mapper.toEntity(request);
        E saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public Optional<Res> update(Integer id, Req request) {
        Optional<E> existing = repository.findByIdAndIsActiveTrue(id);
        if (!existing.isPresent()) {
            return Optional.empty();
        }
        E entity = existing.get();
        mapper.updateEntity(entity, request);
        E saved = repository.save(entity);
        return Optional.of(mapper.toDto(saved));
    }

    @Transactional
    public boolean delete(Integer id) {
        Optional<E> existing = repository.findByIdAndIsActiveTrue(id);
        if (!existing.isPresent()) {
            return false;
        }
        E entity = existing.get();
        // Use reflection to set active flag in a best-effort way. Different domain classes may
        // expose either setActive(boolean) or setIsActive(boolean) or a field named 'active'/'isActive'.
        try {
            java.lang.reflect.Method m = null;
            try {
                m = entity.getClass().getMethod("setActive", boolean.class);
            } catch (NoSuchMethodException ignored) {
                try {
                    m = entity.getClass().getMethod("setIsActive", boolean.class);
                } catch (NoSuchMethodException ignored2) {
                    m = null;
                }
            }

            if (m != null) {
                m.invoke(entity, false);
            } else {
                // try fields
                try {
                    java.lang.reflect.Field f = null;
                    try {
                        f = entity.getClass().getDeclaredField("active");
                    } catch (NoSuchFieldException ignored) {
                        try {
                            f = entity.getClass().getDeclaredField("isActive");
                        } catch (NoSuchFieldException ignored2) {
                            f = null;
                        }
                    }
                    if (f != null) {
                        f.setAccessible(true);
                        f.set(entity, false);
                    }
                } catch (Exception ex) {
                    // ignore
                }
            }
        } catch (Exception e) {
            // ignore any reflection issues
        }
        repository.save(entity);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Res> getAllAsList() {
        java.util.List<E> entities = repository.findByIsActiveTrue();
        return entities.stream().map(mapper::toDto).collect(java.util.stream.Collectors.toList());
    }
}
