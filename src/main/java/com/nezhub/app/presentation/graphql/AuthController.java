package com.nezhub.app.presentation.graphql;

import com.nezhub.app.application.dto.request.LoginRequest;
import com.nezhub.app.application.dto.request.RegisterRequest;
import com.nezhub.app.application.dto.response.AuthResponse;
import com.nezhub.app.application.service.AuthService;
import com.nezhub.app.domain.enums.UserRole;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @MutationMapping
    public AuthResponse register(
            @Argument String username,
            @Argument String email,
            @Argument String password,
            @Argument UserRole role
    ) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setRole(role);
        return authService.register(request);
    }

    @MutationMapping
    public AuthResponse login(
            @Argument String email,
            @Argument String password
    ) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return authService.login(request);
    }
}
