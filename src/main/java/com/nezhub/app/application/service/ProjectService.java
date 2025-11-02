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

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(CreateProjectRequest request, String userId) {
        Project project = new Project();
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setGoals(request.getGoals() != null ? request.getGoals() : new ArrayList<>());
        project.setRequiredSkills(request.getRequiredSkills());
        project.setStatus(ProjectStatus.OPEN);
        project.setCreatorId(userId);
        project.setCollaborators(new ArrayList<>());
        project.setVotes(0);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    @CacheEvict(value = {"projectDetails", "trendingProjects"}, allEntries = true)
    public Project updateProject(String projectId, UpdateProjectRequest request, String userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con ID: " + projectId));

        if (!project.getCreatorId().equals(userId)) {
            throw new UnauthorizedOperationException("Solo el creador puede actualizar este proyecto");
        }

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

    @Cacheable(value = "projectDetails", key = "#projectId")
    public Project getProjectById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proyecto no encontrado con ID: " + projectId));
    }
}
