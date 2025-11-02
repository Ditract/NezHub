package com.nezhub.app.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;


public class CreateProjectRequest {

    @NotBlank(message = "El título es requerido")
    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    private String title;

    @NotBlank(message = "La descripción es requerida")
    @Size(min = 50, message = "La descripción debe tener mínimo 50 caracteres")
    private String description;

    private List<String> goals;

    @NotEmpty(message = "Debe especificar al menos 1 habilidad requerida")
    private List<String> requiredSkills;

    public CreateProjectRequest(){}

    public CreateProjectRequest(String title, String description, List<String> goals, List<String> requiredSkills) {
        this.title = title;
        this.description = description;
        this.goals = goals;
        this.requiredSkills = requiredSkills;
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
}