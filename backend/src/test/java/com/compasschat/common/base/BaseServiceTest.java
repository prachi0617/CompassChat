package com.compasschat.common.base;

import com.compasschat.common.base.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    /** Minimal concrete entity for testing the abstract base class. */
    static class StubEntity extends AuditableEntity {}

    /** Concrete service subclass that delegates entirely to BaseService. */
    static class StubService extends BaseService<StubEntity, UUID> {
        StubService(BaseRepository<StubEntity, UUID> repo) {
            super(repo, "StubEntity");
        }
    }

    @Mock
    private BaseRepository<StubEntity, UUID> repository;

    private StubService service;

    @BeforeEach
    void setUp() {
        service = new StubService(repository);
    }

    // --- findById ---

    @Test
    void shouldReturnEntity_whenFindByIdAndEntityExists() {
        UUID id = UUID.randomUUID();
        StubEntity entity = new StubEntity();
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        StubEntity result = service.findById(id);

        assertSame(entity, result);
    }

    @Test
    void shouldThrowResourceNotFoundException_whenFindByIdAndEntityMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }

    // --- findAll ---

    @Test
    void shouldDelegateToRepository_whenFindAllCalled() {
        StubEntity e1 = new StubEntity();
        StubEntity e2 = new StubEntity();
        when(repository.findAll()).thenReturn(List.of(e1, e2));

        List<StubEntity> result = service.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyList_whenFindAllAndNoEntities() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(service.findAll().isEmpty());
    }

    // --- save ---

    @Test
    void shouldDelegateToRepository_whenSaveCalled() {
        StubEntity entity = new StubEntity();
        StubEntity saved = new StubEntity();
        when(repository.save(entity)).thenReturn(saved);

        StubEntity result = service.save(entity);

        assertSame(saved, result);
    }

    // --- delete ---

    @Test
    void shouldDeleteEntity_whenDeleteCalledAndEntityExists() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repository).deleteById(id);
    }

    @Test
    void shouldThrowResourceNotFoundException_whenDeleteCalledAndEntityMissing() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
        verify(repository, never()).deleteById(any());
    }
}
