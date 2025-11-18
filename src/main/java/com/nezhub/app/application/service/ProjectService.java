package com.nezhub.app.application.service;

import com.nezhub.app.application.dto.request.CreateProjectRequest;
import com.nezhub.app.application.dto.request.UpdateProjectRequest;
import com.nezhub.app.application.exception.ProjectNotFoundException;
import com.nezhub.app.application.exception.UnauthorizedOperationException;
import com.nezhub.app.domain.enums.ProjectStatus;
import com.nezhub.app.domain.model.Project;
import com.nezhub.app.domain.repository.ProjectRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    @CacheEvict(value = "trendingProjects", allEntries = true)
    public Project createProject(CreateProjectRequest request, String userId) {
        LocalDateTime now = LocalDateTime.now();

        Project project = new Project(
                null, //
                request.getTitle(),
                request.getDescription(),
                request.getGoals() != null ? request.getGoals() : new ArrayList<>(),
                request.getRequiredSkills(),
                ProjectStatus.OPEN,
                userId,
                new ArrayList<>(), // colaboradores vacíos
                0, // votos iniciales
                now, // createdAt
                now  // updatedAt
        );

        return projectRepository.save(project);
    }



    @CacheEvict(value = {"projectDetails", "trendingProjects"}, allEntries = true)
    public Project updateProject(String projectId, UpdateProjectRequest request, String userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con ID: " + projectId));


        if (!project.getCreatorId().equals(userId)) {
            throw new UnauthorizedOperationException("Solo el creador puede actualizar este proyecto");
        }

        // Actualizar campos (solo si no son null)
        if (request.getTitle() != null) {
            project.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getGoals() != null) {
            project.setGoals(request.getGoals());
        }
        if (request.getRequiredSkills() != null) {
            project.setRequiredSkills(request.getRequiredSkills());
        }
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }

        project.setUpdatedAt(LocalDateTime.now());

        return projectRepository.save(project);
    }


    @CacheEvict(value = {"projectDetails", "trendingProjects"}, allEntries = true)
    public void deleteProject(String projectId, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con ID: " + projectId));

        if (!project.getCreatorId().equals(userId)) {
            throw new UnauthorizedOperationException("Solo el creador puede eliminar este proyecto");
        }

        projectRepository.delete(project);
    }

    // TODO: Corregir error en caché (de momento funciona si quitamos caché)
    @Cacheable(value = "projectDetails", key = "#projectId")
    public Project getProjectById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con ID: " + projectId));
    }


    @Cacheable(value = "trendingProjects", key = "'trending_' + #limit")
    public List<Project> getTrendingProjects(int limit) {
        return projectRepository.findByStatusOrderByVotesDesc(
                ProjectStatus.OPEN,
                org.springframework.data.domain.PageRequest.of(0, limit)
        ).getContent();
    }


    @Cacheable(value = "searchBySkill", key = "#skill")
    public List<Project> searchBySkill(String skill) {
        return projectRepository.findByRequiredSkillsContaining(skill);
    }


    @CacheEvict(value = "trendingProjects", allEntries = true)
    public void invalidateTrendingCache() {
        // Método vacío - solo invalida el caché
    }

    /**
     * Obtiene todos los proyectos.
     * Sin caché porque puede retornar muchos registros.
     */
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    //Obtiene proyecto con paginación
    public List<Project> findAllPaginated(int page, int size) {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(page, size);
        return projectRepository.findAll(pageable).getContent();
    }

}

