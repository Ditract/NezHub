package com.nezhub.app.application.dto.request;

import jakarta.validation.constraints.NotBlank;


public class JoinProjectRequest {

    @NotBlank(message = "El ID del proyecto es requerido")
    private String projectId;

    public JoinProjectRequest() {
    }

    public JoinProjectRequest(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

}
