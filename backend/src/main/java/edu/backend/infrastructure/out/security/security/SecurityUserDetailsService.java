package edu.backend.infrastructure.out.security.security;

import edu.backend.infrastructure.out.persistence.UserJpaEntity;
import edu.backend.infrastructure.out.persistence.UserJpaRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    public SecurityUserDetailsService(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserJpaEntity user = userJpaRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CurrentUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                edu.backend.domain.model.UserRole.valueOf(user.getRole()),
                user.isActive()
        );
    }
}