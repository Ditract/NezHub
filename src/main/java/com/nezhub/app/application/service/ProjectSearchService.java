package com.nezhub.app.application.service;

import com.nezhub.app.domain.enums.ProjectStatus;
import com.nezhub.app.domain.model.Project;
import com.nezhub.app.domain.repository.ProjectRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ProjectSearchService {
    private final ProjectRepository projectRepository;

    public ProjectSearchService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Busca proyectos por skill con caché.
     *
     * CACHÉ:
     * - Key: skill específico
     * - TTL: 30 minutos
     */
    @Cacheable(value = "searchBySkill", key = "#skill")
    public List<Project> searchBySkill(String skill) {
        return projectRepository.findByRequiredSkillsContaining(skill);
    }


    public List<Project> searchByStatus(ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }


    public List<Project> searchByCreator(String creatorId) {
        return projectRepository.findByCreatorId(creatorId);
    }

    /**
     * Búsqueda avanzada con múltiples filtros.
     *
     * - Si ambos filtros (skill + status): usa método optimizado con índice compuesto
     * - Si solo skill: usa searchBySkill (con caché)
     * - Si solo status: usa searchByStatus
     * - Si ninguno: retorna todos
     *
     * CACHÉ:
     * - Solo se cachea cuando se busca por skill únicamente
     * - Combinaciones no se cachean
     */
    public List<Project> searchWithFilters(String skill, ProjectStatus status) {

        if (skill != null && status != null) {
            return projectRepository.findByRequiredSkillsContainingAndStatus(skill, status);
        }

        if (skill != null) {
            return searchBySkill(skill);
        }

        if (status != null) {
            return searchByStatus(status);
        }

        return projectRepository.findAll();
    }


    public List<Project> searchByCreatorAndStatus(String creatorId, ProjectStatus status) {
        return projectRepository.findByCreatorIdAndStatus(creatorId, status);
    }
}

