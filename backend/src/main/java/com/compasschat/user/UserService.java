package com.compasschat.user;

import com.compasschat.common.base.BaseService;
import com.compasschat.common.base.exception.ResourceNotFoundException;
import com.compasschat.user.dto.UpdateUserRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService extends BaseService<User, UUID> {

    private final UserRepository users; // typed copy for our extra methods

    public UserService(UserRepository users) {
        super(users, "User"); // hand the remote its batteries + a name for errors
        this.users = users;
    }

    public User findByUsername(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", username));
    }

    /** Apply the edit form: null boxes mean "leave it alone." */
    public User updateProfile(UUID id, UpdateUserRequest req) {
        User user = findById(id); // inherited! throws a clean 404 if missing

        if (req.username() != null && !req.username().isBlank()) {
            if (users.existsByUsername(req.username())) {
                throw new IllegalStateException("That username is taken");
            }
            // (a setter for username would go on the entity - add if you allow renames)
        }
        if (req.email() != null && !req.email().isBlank()) {
            user.setEmail(req.email());
        }

        return save(user);
    }
}
