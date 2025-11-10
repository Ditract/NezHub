package com.nezhub.app.application.service;

import com.nezhub.app.application.exception.*;
import com.nezhub.app.domain.enums.CollaborationStatus;
import com.nezhub.app.domain.enums.ProjectStatus;
import com.nezhub.app.domain.model.Collaboration;
import com.nezhub.app.domain.model.Project;
import com.nezhub.app.domain.repository.CollaborationRepository;
import com.nezhub.app.domain.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar colaboraciones en proyectos.
 * - Crear solicitudes de colaboración
 * - Aprobar/rechazar solicitudes
 * - Gestionar colaboradores en proyectos
 */
@Service
public class CollaborationService {

    private final CollaborationRepository collaborationRepository;
    private final ProjectRepository projectRepository;

    public CollaborationService(CollaborationRepository collaborationRepository,
                                ProjectRepository projectRepository) {
        this.collaborationRepository = collaborationRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Usuario solicita unirse a un proyecto.
     * Se valida
     * RESULTADO: Crea Collaboration con status PENDING
     */
    public Collaboration joinProject(String projectId, String userId) {
        // Validar que proyecto existe
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));
        // Validar que proyecto está OPEN
        if (project.getStatus() != ProjectStatus.OPEN) {
            throw new ProjectNotOpenException("El proyecto no está aceptando colaboradores");
        }
        // Validar que usuario no es el creador
        if (project.getCreatorId().equals(userId)) {
            throw new InvalidProjectDataException("No puedes unirte a tu propio proyecto");
        }
        // Validar que no tenga solicitud previa
        Optional<Collaboration> existing = collaborationRepository
                .findByProjectIdAndUserId(projectId, userId);
        if (existing.isPresent()) {
            CollaborationStatus status = existing.get().getStatus();
            if (status == CollaborationStatus.PENDING) {
                throw new CollaborationAlreadyExistsException("Ya tienes una solicitud pendiente");
            } else if (status == CollaborationStatus.APPROVED) {
                throw new CollaborationAlreadyExistsException("Ya eres colaborador de este proyecto");
            }
        }
        // Crear colaboración PENDING
        Collaboration collaboration = new Collaboration(
                projectId,
                userId,
                CollaborationStatus.PENDING,
                LocalDateTime.now(),
                null
        );
        return collaborationRepository.save(collaboration);
    }

    /**
     * Creador aprueba una solicitud de colaboración.
     * Se valida
     * RESULTADO:
     * - Collaboration.status = APPROVED
     * - userId agregado a project.collaborators[]
     */
    @Transactional
    public Collaboration approveCollaboration(String collaborationId, String creatorId) {
        // Obtener colaboración
        Collaboration collaboration = collaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new CollaborationNotFoundException("Colaboración no encontrada"));
        // Obtener proyecto
        Project project = projectRepository.findById(collaboration.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));
        // Validar que quien aprueba es el creador
        if (!project.getCreatorId().equals(creatorId)) {
            throw new UnauthorizedOperationException("Solo el creador puede aprobar colaboradores");
        }
        // Validar que esté PENDING
        if (collaboration.getStatus() != CollaborationStatus.PENDING) {
            throw new InvalidProjectDataException("Solo se pueden aprobar solicitudes PENDING");
        }
        // Crear nueva instancia INMUTABLE con estado APPROVED
        Collaboration approvedCollaboration = new Collaboration(
                collaboration.getProjectId(),
                collaboration.getUserId(),
                CollaborationStatus.APPROVED,
                collaboration.getRequestedAt(),
                LocalDateTime.now()
        );
        collaborationRepository.save(approvedCollaboration);
        // Agregar colaborador al proyecto
        if (!project.getCollaborators().contains(collaboration.getUserId())) {
            project.getCollaborators().add(collaboration.getUserId());
            project.setUpdatedAt(LocalDateTime.now());
            projectRepository.save(project);
        }
        return approvedCollaboration;
    }

    /**
     * Creador rechaza una solicitud de colaboración.
     * Se valida
     * RESULTADO: Collaboration.status = REJECTED
     */
    public Collaboration rejectCollaboration(String collaborationId, String creatorId) {
        // Obtener colaboración
        Collaboration collaboration = collaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new CollaborationNotFoundException("Colaboración no encontrada"));
        // Obtener proyecto
        Project project = projectRepository.findById(collaboration.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado"));
        // Validar que quien rechaza es el creador
        if (!project.getCreatorId().equals(creatorId)) {
            throw new UnauthorizedOperationException("Solo el creador puede rechazar colaboradores");
        }
        // Validar que esté PENDING
        if (collaboration.getStatus() != CollaborationStatus.PENDING) {
            throw new InvalidProjectDataException("Solo se pueden rechazar solicitudes PENDING");
        }

        Collaboration rejectedCollaboration = new Collaboration(
                collaboration.getProjectId(),
                collaboration.getUserId(),
                CollaborationStatus.REJECTED,
                collaboration.getRequestedAt(),
                LocalDateTime.now()
        );
        return collaborationRepository.save(rejectedCollaboration);
    }

    /**
     * Obtiene todas las colaboraciones de un proyecto.
     */
    public List<Collaboration> getProjectCollaborations(String projectId) {
        return collaborationRepository.findByProjectId(projectId);
    }

    /**
     * Obtiene colaboraciones de un proyecto por estado.
     */
    public List<Collaboration> getProjectCollaborationsByStatus(String projectId, CollaborationStatus status) {
        return collaborationRepository.findByProjectIdAndStatus(projectId, status);
    }
}
