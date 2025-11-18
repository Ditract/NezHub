package com.nezhub.app.presentation.graphql;

import com.nezhub.app.application.service.UserService;
import com.nezhub.app.application.service.VoteService;
import com.nezhub.app.domain.model.Project;
import com.nezhub.app.domain.model.User;
import com.nezhub.app.infrastructure.security.AuthenticationUtils;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;


@Controller
public class VoteController {

    private final VoteService voteService;
    private final UserService userService;

    public VoteController(VoteService voteService, UserService userService) {
        this.voteService = voteService;
        this.userService = userService;
    }

    /**
     * Mutation: Usuario vota un proyecto.
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Project voteProject(@Argument String projectId) {
        String email = AuthenticationUtils.getCurrentUserEmail();
        User user = userService.findByEmail(email).orElseThrow();
        return voteService.voteProject(projectId, user.getId());
    }

    /**
     * Mutation: Usuario retira su voto.
     */
    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Project unvoteProject(@Argument String projectId) {
        String email = AuthenticationUtils.getCurrentUserEmail();
        User user = userService.findByEmail(email).orElseThrow();
        return voteService.unvoteProject(projectId, user.getId());
    }

    /**
     * Query: Verifica si usuario actual ha votado un proyecto.
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean hasVoted(@Argument String projectId) {
        String email = AuthenticationUtils.getCurrentUserEmail();
        User user = userService.findByEmail(email).orElseThrow();
        return voteService.hasUserVoted(projectId, user.getId());
    }
}

