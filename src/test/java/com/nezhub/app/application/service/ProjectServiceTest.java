package com.nezhub.app.application.service;

import com.nezhub.app.application.dto.request.CreateProjectRequest;
import com.nezhub.app.application.dto.request.UpdateProjectRequest;
import com.nezhub.app.application.exception.ProjectNotFoundException;
import com.nezhub.app.application.exception.UnauthorizedOperationException;
import com.nezhub.app.domain.enums.ProjectStatus;
import com.nezhub.app.domain.model.Project;
import com.nezhub.app.domain.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ProjectService.
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private CreateProjectRequest validCreateRequest;
    private UpdateProjectRequest validUpdateRequest;
    private Project savedProject;
    private String creatorId = "user123";
    private String otherUserId = "user456";

    @BeforeEach
    void setUp() {
        validCreateRequest = new CreateProjectRequest(
                "Sistema de Tareas",
                "Desarrollar una aplicación web que use IA para priorizar tareas automáticamente",
                Arrays.asList("Implementar JWT", "Integrar IA"),
                Arrays.asList("React", "Node.js", "Python")
        );

        validUpdateRequest = new UpdateProjectRequest(
                "Nuevo título",
                ProjectStatus.IN_PROGRESS
        );

        savedProject = new Project(
                "proj123",
                "Sistema de Tareas",
                "Desarrollar una aplicación web que use IA para priorizar tareas automáticamente",
                Arrays.asList("Implementar JWT", "Integrar IA"),
                Arrays.asList("React", "Node.js", "Python"),
                ProjectStatus.OPEN,
                creatorId,
                new ArrayList<>(),
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    /**
     * Test 1: Crear proyecto exitoso.
     *
     * GIVEN: Datos válidos
     * WHEN: Usuario crea proyecto
     * THEN: Se guarda proyecto con status OPEN, votes 0, creatorId correcto
     */
    @Test
    void testCreateProject_Success() {
        // Arrange
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        // Act
        Project result = projectService.createProject(validCreateRequest, creatorId);

        // Assert
        assertNotNull(result);
        assertEquals("Sistema de Tareas", result.getTitle());
        assertEquals(creatorId, result.getCreatorId());
        assertEquals(ProjectStatus.OPEN, result.getStatus());
        assertEquals(0, result.getVotes());
        assertNotNull(result.getCollaborators());

        verify(projectRepository, times(1)).save(any(Project.class));
    }

    /**
     * Test 2: Actualizar proyecto por creador (exitoso).
     *
     * GIVEN: Usuario es el creador
     * WHEN: Actualiza proyecto
     * THEN: Proyecto actualizado correctamente
     */
    @Test
    void testUpdateProject_ByCreator_Success() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(savedProject));
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        // Act
        Project result = projectService.updateProject("proj123", validUpdateRequest, creatorId);

        // Assert
        assertNotNull(result);
        verify(projectRepository, times(1)).findById("proj123");
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    /**
     * Test 3: Actualizar proyecto por no-creador (debe fallar).
     *
     * GIVEN: Usuario NO es el creador
     * WHEN: Intenta actualizar proyecto
     * THEN: Lanza UnauthorizedOperationException
     */
    @Test
    void testUpdateProject_ByNonCreator_ThrowsException() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(savedProject));

        // Act & Assert
        UnauthorizedOperationException exception = assertThrows(
                UnauthorizedOperationException.class,
                () -> projectService.updateProject("proj123", validUpdateRequest, otherUserId)
        );

        assertTrue(exception.getMessage().contains("creador"));
        verify(projectRepository, never()).save(any(Project.class));
    }

    /**
     * Test 4: Actualizar proyecto que no existe (debe fallar).
     *
     * GIVEN: Proyecto no existe
     * WHEN: Intenta actualizar
     * THEN: Lanza ProjectNotFoundException
     */
    @Test
    void testUpdateProject_NotFound() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ProjectNotFoundException exception = assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.updateProject("proj999", validUpdateRequest, creatorId)
        );

        assertTrue(exception.getMessage().contains("no encontrado"));
    }

    /**
     * Test 5: Eliminar proyecto por creador (exitoso).
     *
     * GIVEN: Usuario es el creador
     * WHEN: Elimina proyecto
     * THEN: Proyecto eliminado correctamente
     */
    @Test
    void testDeleteProject_ByCreator_Success() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(savedProject));
        doNothing().when(projectRepository).delete(any(Project.class));

        // Act
        assertDoesNotThrow(() -> projectService.deleteProject("proj123", creatorId));

        // Assert
        verify(projectRepository, times(1)).findById("proj123");
        verify(projectRepository, times(1)).delete(savedProject);
    }

    /**
     * Test 6: Eliminar proyecto por no-creador (debe fallar).
     *
     * GIVEN: Usuario NO es el creador
     * WHEN: Intenta eliminar proyecto
     * THEN: Lanza UnauthorizedOperationException
     */
    @Test
    void testDeleteProject_ByNonCreator_ThrowsException() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(savedProject));

        // Act & Assert
        UnauthorizedOperationException exception = assertThrows(
                UnauthorizedOperationException.class,
                () -> projectService.deleteProject("proj123", otherUserId)
        );

        assertTrue(exception.getMessage().contains("creador"));
        verify(projectRepository, never()).delete(any(Project.class));
    }

    /**
     * Test 7: Obtener proyecto por ID (exitoso).
     *
     * GIVEN: Proyecto existe
     * WHEN: Busca por ID
     * THEN: Retorna proyecto
     */
    @Test
    void testGetProjectById_Success() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(savedProject));

        // Act
        Project result = projectService.getProjectById("proj123");

        // Assert
        assertNotNull(result);
        assertEquals("proj123", result.getId());
        assertEquals("Sistema de Tareas", result.getTitle());
        verify(projectRepository, times(1)).findById("proj123");
    }

    /**
     * Test 8: Obtener proyecto que no existe (debe fallar).
     *
     * GIVEN: Proyecto no existe
     * WHEN: Busca por ID
     * THEN: Lanza ProjectNotFoundException
     */
    @Test
    void testGetProjectById_NotFound() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ProjectNotFoundException exception = assertThrows(
                ProjectNotFoundException.class,
                () -> projectService.getProjectById("proj999")
        );

        assertTrue(exception.getMessage().contains("no encontrado"));
    }
}
