package com.nezhub.app.application.dto.response;

import com.nezhub.app.domain.enums.CollaborationStatus;
import java.time.LocalDateTime;


public class CollaborationResponse {

    private String id;
    private String projectId;
    private String userId;
    private String username;  // Resuelto con @SchemaMapping
    private CollaborationStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;

    public CollaborationResponse() {
    }

    public CollaborationResponse(String id, String projectId, String userId, String username,
                                 CollaborationStatus status, LocalDateTime requestedAt, LocalDateTime respondedAt) {
        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.username = username;
        this.status = status;
        this.requestedAt = requestedAt;
        this.respondedAt = respondedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CollaborationStatus getStatus() {
        return status;
    }

    public void setStatus(CollaborationStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

}
