package com.nezhub.app.presentation.graphql;

import com.nezhub.app.application.service.ProjectService;
import com.nezhub.app.application.service.UserService;
import com.nezhub.app.domain.model.Project;
import com.nezhub.app.domain.model.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;


@Controller
public class ProjectQueryController {

    private final ProjectService projectService;
    private final UserService userService;

    public ProjectQueryController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }


    @QueryMapping
    public Project getProject(@Argument String id) {
        return projectService.getProjectById(id);
    }


    @QueryMapping
    public List<Project> getAllProjects() {
        return projectService.findAll();
    }

    /**
     * Query para obtener proyectos trending (más votados).
     */
    @QueryMapping
    public List<Project> getTrendingProjects(@Argument(name = "limit") Integer limit) {
        int actualLimit = (limit != null && limit > 0) ? limit : 10;
        return projectService.getTrendingProjects(actualLimit);
    }


    @SchemaMapping(typeName = "Project", field = "creatorUsername")
    public String creatorUsername(Project project) {
        User user = userService.findById(project.getCreatorId());
        return user != null ? user.getUsername() : null;
    }
}