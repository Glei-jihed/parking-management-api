package edu.backend.infrastructure.out.persistence;

import edu.backend.domain.model.User;
import edu.backend.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(PersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public User save(User user) {
        return PersistenceMapper.toDomain(
                userJpaRepository.save(PersistenceMapper.toJpa(user))
        );
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}