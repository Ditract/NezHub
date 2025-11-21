package com.nezhub.app.application.dto.request;

import com.nezhub.app.domain.enums.ProjectStatus;
import jakarta.validation.constraints.Size;

import java.util.List;


public class UpdateProjectRequest {

    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    private String title;

    @Size(min = 50, message = "La descripción debe tener mínimo 50 caracteres")
    private String description;

    private List<String> goals;

    private List<String> requiredSkills;

    private ProjectStatus status;

    public UpdateProjectRequest(){}

    public UpdateProjectRequest
            (String title, String description, List<String> goals,
             List<String> requiredSkills, ProjectStatus status) {
        this.title = title;
        this.description = description;
        this.goals = goals;
        this.requiredSkills = requiredSkills;
        this.status = status;
    }

    public UpdateProjectRequest(String title, ProjectStatus status) {
        this.title = title;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getGoals() {
        return goals;
    }

    public void setGoals(List<String> goals) {
        this.goals = goals;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}