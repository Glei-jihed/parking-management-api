package edu.backend.domain.port.out;

import edu.backend.domain.model.User;

public interface TokenProviderPort {
    String generateToken(User user);
}