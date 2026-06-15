package com.compasschat.common.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T extends AuditableEntity, ID> extends JpaRepository<T, ID> {
}
