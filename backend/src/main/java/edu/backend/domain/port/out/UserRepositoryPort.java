package edu.backend.domain.port.out;

import edu.backend.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAll();
    User save(User user);
    boolean existsByEmail(String email);
}