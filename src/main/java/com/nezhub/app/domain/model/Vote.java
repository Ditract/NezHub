package com.nezhub.app.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "votes")
@CompoundIndex(name = "project_user_unique_idx", def = "{'projectId': 1, 'userId': 1}", unique = true)
public class Vote {

    @Id
    private String id;

    @Indexed
    private String projectId;

    @Indexed
    private String userId;

    private LocalDateTime createdAt;

    public Vote() {
    }

    public Vote(String id, String projectId, String userId, LocalDateTime createdAt) {
        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}