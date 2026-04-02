package edu.backend.domain.model;

public class User {

    private Long id;
    private String email;
    private String password;
    private UserRole role;
    private boolean active;

    public User(Long id, String email, String password, UserRole role, boolean active) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}