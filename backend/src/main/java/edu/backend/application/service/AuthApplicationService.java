package edu.backend.application.service;

import edu.backend.domain.exception.UnauthorizedException;
import edu.backend.domain.model.User;
import edu.backend.domain.port.out.PasswordHasherPort;
import edu.backend.domain.port.out.TokenProviderPort;
import edu.backend.domain.port.out.UserRepositoryPort;

public class AuthApplicationService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;
    private final TokenProviderPort tokenProviderPort;

    public AuthApplicationService(
            UserRepositoryPort userRepositoryPort,
            PasswordHasherPort passwordHasherPort,
            TokenProviderPort tokenProviderPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordHasherPort = passwordHasherPort;
        this.tokenProviderPort = tokenProviderPort;
    }

    public String login(String email, String password) {
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.isActive()) {
            throw new UnauthorizedException("User is inactive");
        }

        if (!passwordHasherPort.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return tokenProviderPort.generateToken(user);
    }
}