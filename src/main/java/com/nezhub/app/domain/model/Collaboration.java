package com.nezhub.app.domain.model;

import com.nezhub.app.domain.enums.CollaborationStatus;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "colaborations")
@CompoundIndexes({
        @CompoundIndex(name = "project_status_idx", def = "{'projectId': 1, 'status': 1}"),
        @CompoundIndex(name = "user_project_unique_idx", def = "{'userId': 1, 'projectId': 1}", unique = true),
        @CompoundIndex(name = "project_user_status_idx", def = "{'projectId': 1, 'userId': 1, 'status': 1}")
})
public class Collaboration {

    private String id;
    private String projectId;
    private String userId;
    private CollaborationStatus status = CollaborationStatus.PENDING;

    private LocalDateTime requestedAt;

    private LocalDateTime respondedAt;


    public Collaboration(){}

    public Collaboration(String id, String projectId,
                         String userId, CollaborationStatus status,
                         LocalDateTime requestedAt, LocalDateTime respondedAt
    ) {
        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.status = status;
        this.requestedAt = requestedAt;
        this.respondedAt = respondedAt;
    }
    public Collaboration(String projectId,
                         String userId, CollaborationStatus status,
                         LocalDateTime requestedAt, LocalDateTime respondedAt
    ) {
        this.projectId = projectId;
        this.userId = userId;
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
