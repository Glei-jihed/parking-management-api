package edu.backend.infrastructure.out.security.security;

import edu.backend.domain.model.User;
import edu.backend.domain.model.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CurrentUser implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final UserRole role;
    private final boolean active;

    public CurrentUser(Long id, String email, String password, UserRole role, boolean active) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public UserRole getRole() {
        return role;
    }

    public User toDomainUser() {
        return new User(id, email, password, role, active);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of((GrantedAuthority) () -> "ROLE_" + role.name());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}