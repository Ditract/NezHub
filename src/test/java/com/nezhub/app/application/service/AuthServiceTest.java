package com.nezhub.app.application.service;

import com.nezhub.app.application.dto.request.LoginRequest;
import com.nezhub.app.application.dto.request.RegisterRequest;
import com.nezhub.app.application.dto.response.AuthResponse;
import com.nezhub.app.application.exception.InvalidCredentialsException;
import com.nezhub.app.application.exception.UserAlreadyExistsException;
import com.nezhub.app.domain.enums.UserRole;
import com.nezhub.app.domain.model.User;
import com.nezhub.app.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User savedUser;

    /**
     * Setup ejecutado antes de cada test.
     * Prepara datos de prueba comunes.
     */
    @BeforeEach
    void setUp() {

        validRegisterRequest = new RegisterRequest(
                "john_doe",
                "john@example.com",
                "password123",
                UserRole.CREATOR
        );

        validLoginRequest = new LoginRequest(
                "john@example.com",
                "password123"
        );

        savedUser = new User(
                "user123",
                "john_doe",
                "john@example.com",
                "$2a$10$hashedPassword",
                UserRole.CREATOR,
                LocalDateTime.now()
        );
    }

    /**
     * Test 1: Registro exitoso.
     *
     * GIVEN: Email no existe, datos válidos
     * WHEN: Usuario se registra
     * THEN: Se crea usuario, se genera token, retorna AuthResponse
     */
    @Test
    void testRegister_Success() {
        // Arrange (preparar)
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");
        when(userService.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(anyString(), any(UserRole.class))).thenReturn("fake-jwt-token");


        AuthResponse response = authService.register(validRegisterRequest);


        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("john_doe", response.getUsername());
        assertEquals(UserRole.CREATOR, response.getRole());

        // Verificar que se llamaron los métodos correctos
        verify(userService, times(1)).existsByEmail("john@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userService, times(1)).save(any(User.class));
        verify(jwtUtil, times(1)).generateToken("john@example.com", UserRole.CREATOR);
    }

    /**
     * Test 2: Registro con email duplicado.
     *
     * GIVEN: Email ya existe
     * WHEN: Usuario intenta registrarse
     * THEN: Lanza UserAlreadyExistsException
     */
    @Test
    void testRegister_EmailAlreadyExists() {
        // Arrange
        when(userService.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(validRegisterRequest)
        );

        assertTrue(exception.getMessage().contains("ya está registrado"));

        // Verificar que NO se intentó guardar usuario
        verify(userService, never()).save(any(User.class));
    }

    /**
     * Test 3: Login exitoso.
     *
     * GIVEN: Usuario existe, password correcto
     * WHEN: Usuario hace login
     * THEN: Retorna token JWT y datos de usuario
     */
    @Test
    void testLogin_Success() {
        // Arrange
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), any(UserRole.class))).thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.login(validLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("john_doe", response.getUsername());
        assertEquals(UserRole.CREATOR, response.getRole());

        verify(userService, times(1)).findByEmail("john@example.com");
        verify(passwordEncoder, times(1)).matches("password123", "$2a$10$hashedPassword");
        verify(jwtUtil, times(1)).generateToken("john@example.com", UserRole.CREATOR);
    }

    /**
     * Test 4: Login con credenciales incorrectas - Email no existe.
     *
     * GIVEN: Email no existe en BD
     * WHEN: Usuario intenta login
     * THEN: Lanza InvalidCredentialsException
     */
    @Test
    void testLogin_UserNotFound() {
        // Arrange
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(validLoginRequest)
        );

        assertTrue(exception.getMessage().contains("inválidas"));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    /**
     * Test 5: Login con credenciales incorrectas - Password incorrecto.
     *
     * GIVEN: Email existe, pero password es incorrecto
     * WHEN: Usuario intenta login
     * THEN: Lanza InvalidCredentialsException
     */
    @Test
    void testLogin_WrongPassword() {
        // Arrange
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(validLoginRequest)
        );

        assertTrue(exception.getMessage().contains("inválidas"));
        verify(jwtUtil, never()).generateToken(anyString(), any(UserRole.class));
    }
}

