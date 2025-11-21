package com.nezhub.app.application.service;

import com.nezhub.app.application.exception.CollaborationAlreadyExistsException;
import com.nezhub.app.application.exception.InvalidProjectDataException;
import com.nezhub.app.application.exception.ProjectNotFoundException;
import com.nezhub.app.application.exception.UnauthorizedOperationException;
import com.nezhub.app.domain.enums.CollaborationStatus;
import com.nezhub.app.domain.enums.ProjectStatus;
import com.nezhub.app.domain.model.Collaboration;
import com.nezhub.app.domain.model.Project;
import com.nezhub.app.domain.repository.CollaborationRepository;
import com.nezhub.app.domain.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CollaborationService.
 */
@ExtendWith(MockitoExtension.class)
class CollaborationServiceTest {

    @Mock
    private CollaborationRepository collaborationRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private CollaborationService collaborationService;

    private Project openProject;
    private Project closedProject;
    private Collaboration pendingCollaboration;
    private String projectId = "proj123";
    private String userId = "user456";
    private String creatorId = "user789";

    @BeforeEach
    void setUp() {
        openProject = new Project(
                projectId,
                "Proyecto Open",
                "Descripción de prueba con más de cincuenta caracteres para validación",
                null,                 // goals
                null,                 // requiredSkills
                ProjectStatus.OPEN,
                creatorId,
                new ArrayList<>(),
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        closedProject = new Project(
                "proj999",
                "Proyecto Cerrado",
                "Descripción de prueba con más de cincuenta caracteres para validación",
                null,
                null,
                ProjectStatus.CLOSED,
                creatorId,
                new ArrayList<>(),
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        pendingCollaboration = new Collaboration(
                "collab123",
                projectId,
                userId,
                CollaborationStatus.PENDING,
                LocalDateTime.now(),
                null // reviewedAt
        );
    }

    /**
     * Test 1: Unirse a proyecto exitoso.
     *
     * GIVEN: Proyecto OPEN, usuario no es creador, no tiene solicitud previa
     * WHEN: Usuario solicita unirse
     * THEN: Se crea Collaboration con status PENDING
     */
    @Test
    void testJoinProject_Success() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(openProject));
        when(collaborationRepository.findByProjectIdAndUserId(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(collaborationRepository.save(any(Collaboration.class))).thenReturn(pendingCollaboration);

        // Act
        Collaboration result = collaborationService.joinProject(projectId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(CollaborationStatus.PENDING, result.getStatus());
        verify(collaborationRepository, times(1)).save(any(Collaboration.class));
    }

    /**
     * Test 2: Unirse a proyecto inexistente (debe fallar).
     *
     * GIVEN: Proyecto no existe
     * WHEN: Usuario intenta unirse
     * THEN: Lanza ProjectNotFoundException
     */
    @Test
    void testJoinProject_ProjectNotFound() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class,
                () -> collaborationService.joinProject(projectId, userId));

        verify(collaborationRepository, never()).save(any(Collaboration.class));
    }

    /**
     * Test 3: Unirse a proyecto CLOSED (debe fallar).
     *
     * GIVEN: Proyecto está CLOSED
     * WHEN: Usuario intenta unirse
     * THEN: Lanza ProjectNotOpenException
     */
    @Test
    void testJoinProject_ProjectClosed() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(closedProject));

        // Act & Assert
        assertThrows(com.nezhub.app.application.exception.ProjectNotOpenException.class,
                () -> collaborationService.joinProject("proj999", userId));

        verify(collaborationRepository, never()).save(any(Collaboration.class));
    }

    /**
     * Test 4: Unirse a propio proyecto (debe fallar).
     *
     * GIVEN: Usuario es el creador del proyecto
     * WHEN: Intenta unirse a su propio proyecto
     * THEN: Lanza InvalidProjectDataException
     */
    @Test
    void testJoinProject_OwnProject() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(openProject));

        // Act & Assert
        assertThrows(InvalidProjectDataException.class,
                () -> collaborationService.joinProject(projectId, creatorId));

        verify(collaborationRepository, never()).save(any(Collaboration.class));
    }

    /**
     * Test 5: Unirse dos veces (solicitud PENDING duplicada).
     *
     * GIVEN: Usuario ya tiene solicitud PENDING
     * WHEN: Intenta enviar otra solicitud
     * THEN: Lanza CollaborationAlreadyExistsException
     */
    @Test
    void testJoinProject_AlreadyPending() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(openProject));
        when(collaborationRepository.findByProjectIdAndUserId(anyString(), anyString()))
                .thenReturn(Optional.of(pendingCollaboration));

        // Act & Assert
        CollaborationAlreadyExistsException exception = assertThrows(
                CollaborationAlreadyExistsException.class,
                () -> collaborationService.joinProject(projectId, userId)
        );

        assertTrue(exception.getMessage().contains("pendiente"));
        verify(collaborationRepository, never()).save(any(Collaboration.class));
    }

    /**
     * Test 6: Aprobar colaboración por creador (exitoso).
     *
     * GIVEN: Usuario es creador, colaboración está PENDING
     * WHEN: Creador aprueba
     * THEN: Colaboración cambia a APPROVED, userId agregado a project.collaborators[]
     */
    @Test
    void testApproveCollaboration_Success() {
        // Arrange
        when(collaborationRepository.findById(anyString())).thenReturn(Optional.of(pendingCollaboration));
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(openProject));
        when(collaborationRepository.save(any(Collaboration.class))).thenReturn(pendingCollaboration);
        when(projectRepository.save(any(Project.class))).thenReturn(openProject);

        // Act
        Collaboration result = collaborationService.approveCollaboration("collab123", creatorId);

        // Assert
        assertNotNull(result);
        verify(collaborationRepository, times(1)).save(any(Collaboration.class));
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    /**
     * Test 7: Aprobar colaboración por no-creador (debe fallar).
     *
     * GIVEN: Usuario NO es el creador
     * WHEN: Intenta aprobar
     * THEN: Lanza UnauthorizedOperationException
     */
    @Test
    void testApproveCollaboration_NotCreator() {
        // Arrange
        when(collaborationRepository.findById(anyString())).thenReturn(Optional.of(pendingCollaboration));
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(openProject));

        // Act & Assert
        assertThrows(UnauthorizedOperationException.class,
                () -> collaborationService.approveCollaboration("collab123", "otherUser"));

        verify(collaborationRepository, never()).save(any(Collaboration.class));
    }

    /**
     * Test 8: Rechazar colaboración por creador (exitoso).
     *
     * GIVEN: Usuario es creador, colaboración está PENDING
     * WHEN: Creador rechaza
     * THEN: Colaboración cambia a REJECTED
     */
    @Test
    void testRejectCollaboration_Success() {
        // Arrange
        when(collaborationRepository.findById(anyString())).thenReturn(Optional.of(pendingCollaboration));
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(openProject));
        when(collaborationRepository.save(any(Collaboration.class))).thenReturn(pendingCollaboration);

        // Act
        Collaboration result = collaborationService.rejectCollaboration("collab123", creatorId);

        // Assert
        assertNotNull(result);
        verify(collaborationRepository, times(1)).save(any(Collaboration.class));
        verify(projectRepository, never()).save(any(Project.class)); // No se modifica proyecto
    }
}

