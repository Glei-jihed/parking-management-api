package edu.backend.infrastructure.in.web.dto;

import edu.backend.domain.model.User;
import edu.backend.domain.model.UserRole;

public record UserResponse(
        Long id,
        String email,
        UserRole role,
        boolean active
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.isActive());
    }
}