package com.nezhub.app.application.service;

import com.nezhub.app.application.exception.InvalidProjectDataException;
import com.nezhub.app.application.exception.ProjectNotFoundException;
import com.nezhub.app.domain.enums.ProjectStatus;
import com.nezhub.app.domain.model.Project;
import com.nezhub.app.domain.model.Vote;
import com.nezhub.app.domain.repository.ProjectRepository;
import com.nezhub.app.domain.repository.VoteRepository;
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
 * Tests unitarios para VoteService.
 */
@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private VoteService voteService;

    private Project openProject;
    private Project closedProject;
    private Vote vote;
    private String projectId = "proj123";
    private String userId = "user456";

    @BeforeEach
    void setUp() {
        openProject = new Project(
                projectId,
                "Proyecto Votable",
                "Descripción de prueba con más de cincuenta caracteres para validación",
                ProjectStatus.OPEN,
                "creator123",
                new ArrayList<>(),
                5,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        closedProject = new Project(
                "proj999",
                "Proyecto Cerrado",
                "Descripción de prueba con más de cincuenta caracteres para validación",
                ProjectStatus.CLOSED,
                "creator123",
                new ArrayList<>(),
                10,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        vote = new Vote(
                "vote123",
                projectId,
                userId,
                LocalDateTime.now()
        );
    }

    /**
     * Test 1: Votar proyecto exitoso.
     *
     * GIVEN: Proyecto OPEN, usuario no ha votado antes
     * WHEN: Usuario vota
     * THEN: Se crea Vote, se incrementa Project.votes
     */
    @Test
    void testVoteProject_Success() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(openProject));
        when(voteRepository.existsByProjectIdAndUserId(anyString(), anyString())).thenReturn(false);
        when(voteRepository.save(any(Vote.class))).thenReturn(vote);
        when(projectRepository.save(any(Project.class))).thenReturn(openProject);

        // Act
        Project result = voteService.voteProject(projectId, userId);

        // Assert
        assertNotNull(result);
        verify(voteRepository, times(1)).save(any(Vote.class));
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    /**
     * Test 2: Votar proyecto inexistente (debe fallar).
     *
     * GIVEN: Proyecto no existe
     * WHEN: Usuario intenta votar
     * THEN: Lanza ProjectNotFoundException
     */
    @Test
    void testVoteProject_ProjectNotFound() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class,
                () -> voteService.voteProject(projectId, userId));

        verify(voteRepository, never()).save(any(Vote.class));
    }

    /**
     * Test 3: Votar proyecto CLOSED (debe fallar).
     *
     * GIVEN: Proyecto está CLOSED
     * WHEN: Usuario intenta votar
     * THEN: Lanza InvalidProjectDataException
     */
    @Test
    void testVoteProject_ProjectClosed() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(closedProject));

        // Act & Assert
        InvalidProjectDataException exception = assertThrows(
                InvalidProjectDataException.class,
                () -> voteService.voteProject("proj999", userId)
        );

        assertTrue(exception.getMessage().contains("cerrado"));
        verify(voteRepository, never()).save(any(Vote.class));
    }

    /**
     * Test 4: Votar dos veces (debe fallar).
     *
     * GIVEN: Usuario ya votó este proyecto
     * WHEN: Intenta votar nuevamente
     * THEN: Lanza InvalidProjectDataException
     */
    @Test
    void testVoteProject_AlreadyVoted() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(openProject));
        when(voteRepository.existsByProjectIdAndUserId(anyString(), anyString())).thenReturn(true);

        // Act & Assert
        InvalidProjectDataException exception = assertThrows(
                InvalidProjectDataException.class,
                () -> voteService.voteProject(projectId, userId)
        );

        assertTrue(exception.getMessage().contains("votado"));
        verify(voteRepository, never()).save(any(Vote.class));
    }

    /**
     * Test 5: Remover voto exitoso.
     *
     * GIVEN: Proyecto existe, usuario ha votado
     * WHEN: Usuario retira voto
     * THEN: Se elimina Vote, se decrementa Project.votes
     */
    @Test
    void testUnvoteProject_Success() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(openProject));
        when(voteRepository.existsByProjectIdAndUserId(anyString(), anyString())).thenReturn(true);
        doNothing().when(voteRepository).deleteByProjectIdAndUserId(anyString(), anyString());
        when(projectRepository.save(any(Project.class))).thenReturn(openProject);

        // Act
        Project result = voteService.unvoteProject(projectId, userId);

        // Assert
        assertNotNull(result);
        verify(voteRepository, times(1)).deleteByProjectIdAndUserId(projectId, userId);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    /**
     * Test 6: Remover voto sin haber votado (debe fallar).
     *
     * GIVEN: Usuario NO ha votado este proyecto
     * WHEN: Intenta remover voto
     * THEN: Lanza InvalidProjectDataException
     */
    @Test
    void testUnvoteProject_NotVoted() {
        // Arrange
        when(projectRepository.findById(anyString())).thenReturn(Optional.of(openProject));
        when(voteRepository.existsByProjectIdAndUserId(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        InvalidProjectDataException exception = assertThrows(
                InvalidProjectDataException.class,
                () -> voteService.unvoteProject(projectId, userId)
        );

        assertTrue(exception.getMessage().contains("votado"));
        verify(voteRepository, never()).deleteByProjectIdAndUserId(anyString(), anyString());
    }

    /**
     * Test 7: Verificar si usuario votó (ha votado).
     *
     * GIVEN: Usuario ha votado
     * WHEN: Verifica si votó
     * THEN: Retorna true
     */
    @Test
    void testHasUserVoted_True() {
        // Arrange
        when(voteRepository.existsByProjectIdAndUserId(anyString(), anyString())).thenReturn(true);

        // Act
        boolean result = voteService.hasUserVoted(projectId, userId);

        // Assert
        assertTrue(result);
        verify(voteRepository, times(1)).existsByProjectIdAndUserId(projectId, userId);
    }

    /**
     * Test 8: Verificar si usuario votó (no ha votado).
     *
     * GIVEN: Usuario NO ha votado
     * WHEN: Verifica si votó
     * THEN: Retorna false
     */
    @Test
    void testHasUserVoted_False() {
        // Arrange
        when(voteRepository.existsByProjectIdAndUserId(anyString(), anyString())).thenReturn(false);

        // Act
        boolean result = voteService.hasUserVoted(projectId, userId);

        // Assert
        assertFalse(result);
        verify(voteRepository, times(1)).existsByProjectIdAndUserId(projectId, userId);
    }
}
