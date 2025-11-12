package com.nezhub.app.presentation.graphql;

import com.nezhub.app.application.service.CollaborationService;
import com.nezhub.app.application.service.UserService;
import com.nezhub.app.domain.enums.CollaborationStatus;
import com.nezhub.app.domain.model.Collaboration;
import com.nezhub.app.domain.model.User;
import com.nezhub.app.infrastructure.security.AuthenticationUtils;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class CollaborationController {

    private final CollaborationService collaborationService;
    private final UserService userService;


    public CollaborationController(CollaborationService collaborationService, UserService userService) {
        this.collaborationService = collaborationService;
        this.userService = userService;
    }

    /**
     * Mutation: Usuario solicita unirse a proyecto.
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Collaboration joinProject(@Argument String projectId) {
        String email = AuthenticationUtils.getCurrentUserEmail();
        User user = userService.findByEmail(email).orElseThrow();
        return collaborationService.joinProject(projectId, user.getId());
    }

    /**
     * Mutation: Creador aprueba colaboración.
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Collaboration approveCollaboration(@Argument String collaborationId) {
        String email = AuthenticationUtils.getCurrentUserEmail();
        User user = userService.findByEmail(email).orElseThrow();
        return collaborationService.approveCollaboration(collaborationId, user.getId());
    }

    /**
     * Mutation: Creador rechaza colaboración.
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Collaboration rejectCollaboration(@Argument String collaborationId) {
        String email = AuthenticationUtils.getCurrentUserEmail();
        User user = userService.findByEmail(email).orElseThrow();
        return collaborationService.rejectCollaboration(collaborationId, user.getId());
    }

    /**
     * Query: Obtener colaboraciones de un proyecto.
     */
    @QueryMapping
    public List<Collaboration> getProjectCollaborations(
            @Argument String projectId,
            @Argument(name = "status") CollaborationStatus status
    ) {
        if (status != null) {
            return collaborationService.getProjectCollaborationsByStatus(projectId, status);
        }
        return collaborationService.getProjectCollaborations(projectId);
    }


    @SchemaMapping(typeName = "Collaboration", field = "username")
    public String username(Collaboration collaboration) {
        return userService.findById(collaboration.getUserId()).getUsername();
    }
}
