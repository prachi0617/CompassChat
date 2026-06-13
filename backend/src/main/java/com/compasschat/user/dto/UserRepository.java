package com.compasschat.user.dto;

import java.util.Optional;
import java.util.UUID;

import com.compasschat.common.base.BaseRepository;

public interface UserRepository extends BaseRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
}
