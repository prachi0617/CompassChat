package com.compasschat.common.base;

import com.compasschat.common.base.exception.ResourceNotFoundException;

import java.util.List;

public abstract class BaseService<T extends AuditableEntity, ID> {

    protected final BaseRepository<T, ID> repository;

    protected final String entityName;

    protected BaseService(BaseRepository<T, ID> repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    public T findById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(entityName, String.valueOf(id)));
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    public void delete(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(entityName, String.valueOf(id));
        }
        repository.deleteById(id);
    }
}