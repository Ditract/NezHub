package com.nezhub.app.application.service;

import com.nezhub.app.application.exception.InvalidProjectDataException;
import com.nezhub.app.application.exception.ProjectNotFoundException;
import com.nezhub.app.domain.enums.ProjectStatus;
import com.nezhub.app.domain.model.Project;
import com.nezhub.app.domain.model.Vote;
import com.nezhub.app.domain.repository.ProjectRepository;
import com.nezhub.app.domain.repository.VoteRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio para gestionar votos en proyectos.
 * RESPONSABILIDADES:
 * - Registrar votos de usuarios
 * - Prevenir votos duplicados
 * - Actualizar contador en proyecto
 * - Invalidar caché de trending projects
 */
@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final ProjectRepository projectRepository;

    public VoteService(VoteRepository voteRepository, ProjectRepository projectRepository) {
        this.voteRepository = voteRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Usuario vota un proyecto.
     * Se valida
     * 1. Crear registro Vote
     * 2. Incrementar Project.votes
     * 3. Invalidar caché trending
     */
    @Transactional
    @CacheEvict(value = "trendingProjects", allEntries = true)
    public Project voteProject(String projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));
        // Validar que proyecto no está cerrado
        if (project.getStatus() == ProjectStatus.CLOSED) {
            throw new InvalidProjectDataException("No se puede votar un proyecto cerrado");
        }
        // Validar que usuario no ha votado antes
        if (voteRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new InvalidProjectDataException("Ya has votado este proyecto");
        }
        // Crear voto
        Vote vote = new Vote(null, projectId, userId, LocalDateTime.now());
        voteRepository.save(vote);
        // Incrementar contador en proyecto
        project.setVotes(project.getVotes() + 1);
        project.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    /**
     * Usuario retira su voto de un proyecto.
     * Se valida
     * 1. Eliminar registro Vote
     * 2. Decrementar Project.votes
     * 3. Invalidar caché trending
     */
    @Transactional
    @CacheEvict(value = "trendingProjects", allEntries = true)
    public Project unvoteProject(String projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));
        // Validar que usuario ha votado antes
        if (!voteRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new InvalidProjectDataException("No has votado este proyecto");
        }
        // Eliminar voto
        voteRepository.deleteByProjectIdAndUserId(projectId, userId);
        // Decrementar contador
        int newVotes = Math.max(0, project.getVotes() - 1);
        project.setVotes(newVotes);
        project.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    /**
     * Verifica si un usuario ha votado un proyecto.
     */
    public boolean hasUserVoted(String projectId, String userId) {
        return voteRepository.existsByProjectIdAndUserId(projectId, userId);
    }
}
