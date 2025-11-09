package com.nezhub.app.presentation.graphql;

import com.nezhub.app.application.dto.request.CreateProjectRequest;
import com.nezhub.app.application.dto.request.UpdateProjectRequest;
import com.nezhub.app.application.service.ProjectService;
import com.nezhub.app.application.service.UserService;
import com.nezhub.app.domain.enums.ProjectStatus;
import com.nezhub.app.domain.model.Project;
import com.nezhub.app.domain.model.User;
import com.nezhub.app.infrastructure.security.AuthenticationUtils;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;


@Controller
public class ProjectMutationController {

    private final ProjectService projectService;
    private final UserService userService;

    public ProjectMutationController(ProjectService projectService, UserService userService) {
        this.projectService = Objects.requireNonNull(projectService);
        this.userService = Objects.requireNonNull(userService);
    }

    /**
     * Mutation para crear proyecto.
     *
     * GRAPHQL:
     * mutation {
     *   createProject(input: {
     *     title: "Mi Proyecto"
     *     description: "Descripción detallada de al menos 50 caracteres..."
     *     goals: ["Objetivo 1", "Objetivo 2"]
     *     requiredSkills: ["React", "Node.js"]
     *   }) {
     *     id
     *     title
     *     creatorUsername
     *   }
     * }
     *
     * HEADERS REQUERIDOS:
     * Authorization: Bearer <token-jwt>
     *
     * VALIDACIÓN:
     * Solo usuarios autenticados pueden crear proyectos
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Project createProject(@Argument CreateProjectInput input) {

        String email = AuthenticationUtils.getCurrentUserEmail();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        CreateProjectRequest request = new CreateProjectRequest();
        request.setTitle(input.title());
        request.setDescription(input.description());
        request.setGoals(input.goals());
        request.setRequiredSkills(input.requiredSkills());

        return projectService.createProject(request, user.getId());
    }

    /**
     * Mutation para actualizar proyecto.
     *
     * GRAPHQL:
     * mutation {
     *   updateProject(
     *     id: "123"
     *     input: {
     *       title: "Nuevo título"
     *       status: IN_PROGRESS
     *     }
     *   ) {
     *     id
     *     title
     *     status
     *   }
     * }
     *
     * VALIDACIÓN:
     * Solo el creador del proyecto puede actualizarlo
     * (validado en ProjectService)
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Project updateProject(@Argument String id, @Argument UpdateProjectInput input) {

        String email = AuthenticationUtils.getCurrentUserEmail();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setTitle(input.title());
        request.setDescription(input.description());
        request.setGoals(input.goals());
        request.setRequiredSkills(input.requiredSkills());
        request.setStatus(input.status());

        return projectService.updateProject(id, request, user.getId());
    }

    /**
     * Mutation para eliminar proyecto.
     *
     * GRAPHQL:
     * mutation {
     *   deleteProject(id: "123")
     * }
     *
     * VALIDACIÓN:
     * Solo el creador del proyecto puede eliminarlo
     * (validado en ProjectService)
     *
     * RETORNA:
     * true si se eliminó correctamente
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean deleteProject(@Argument String id) {

        String email = AuthenticationUtils.getCurrentUserEmail();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        projectService.deleteProject(id, user.getId());

        return true;
    }


    public record CreateProjectInput(
            String title,
            String description,
            List<String> goals,
            List<String> requiredSkills
    ) {}


    public record UpdateProjectInput(
            String title,
            String description,
            List<String> goals,
            List<String> requiredSkills,
            ProjectStatus status
    ) {}
}

