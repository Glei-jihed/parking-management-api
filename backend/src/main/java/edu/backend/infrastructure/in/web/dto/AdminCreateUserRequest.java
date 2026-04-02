package edu.backend.infrastructure.in.web.dto;

import edu.backend.domain.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminCreateUserRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotNull UserRole role
) {
}