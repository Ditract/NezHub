package com.nezhub.app.presentation.graphql;

import com.nezhub.app.application.service.ProjectSearchService;
import com.nezhub.app.domain.enums.ProjectStatus;
import com.nezhub.app.domain.model.Project;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;


@Controller
public class ProjectSearchController {
    private final ProjectSearchService searchService;

    public ProjectSearchController(ProjectSearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Query de búsqueda con filtros opcionales.
     *
     * CASOS DE USO:
     *
     * 1. Buscar por skill:
     * query {
     *   searchProjects(skill: "React") {
     *     id
     *     title
     *     requiredSkills
     *   }
     * }
     *
     * 2. Buscar por status:
     * query {
     *   searchProjects(status: OPEN) {
     *     id
     *     title
     *     status
     *   }
     * }
     *
     * 3. Buscar por skill + status (usa índice compuesto):
     * query {
     *   searchProjects(skill: "React", status: OPEN) {
     *     id
     *     title
     *     requiredSkills
     *     status
     *   }
     * }
     *
     * 4. Buscar proyectos de un creador:
     * query {
     *   searchProjects(creatorId: "123") {
     *     id
     *     title
     *     creatorUsername
     *   }
     * }
     *
     * 5. Sin filtros (retorna todos):
     * query {
     *   searchProjects {
     *     id
     *     title
     *   }
     * }
     *
     * OPTIMIZACIÓN:
     * - Búsquedas por skill se cachean (30 min)
     * - Combinaciones skill + status usan índice compuesto
     * - Búsquedas por creador son rápidas (índice en creatorId)
     */
    @QueryMapping
    public List<Project> searchProjects(
            @Argument(name = "skill") String skill,
            @Argument(name = "status") ProjectStatus status,
            @Argument(name = "creatorId") String creatorId
    ) {

        if (creatorId != null && status != null) {
            return searchService.searchByCreatorAndStatus(creatorId, status);
        }


        if (creatorId != null) {
            return searchService.searchByCreator(creatorId);
        }

        return searchService.searchWithFilters(skill, status);
    }
}

