package edu.backend.infrastructure.in.web;

import edu.backend.application.service.AuthApplicationService;
import edu.backend.infrastructure.in.web.dto.AuthResponse;
import edu.backend.infrastructure.in.web.dto.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        String token = authApplicationService.login(request.email(), request.password());
        return new AuthResponse(token);
    }
}