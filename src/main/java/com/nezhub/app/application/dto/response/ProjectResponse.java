package com.nezhub.app.application.dto.response;

import com.nezhub.app.domain.enums.ProjectStatus;


import java.time.LocalDateTime;
import java.util.List;


public class ProjectResponse {

    private String id;
    private String title;
    private String description;
    private List<String> goals;
    private List<String> requiredSkills;
    private ProjectStatus status;
    private String creatorId;
    private String creatorUsername;  // Resuelto en GraphQL con @SchemaMapping
    private List<String> collaborators;
    private Integer votes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProjectResponse(){}

    public ProjectResponse(String id, String title, String description, List<String> goals,
                           List<String> requiredSkills, ProjectStatus status, String creatorId,
                           String creatorUsername, List<String> collaborators, Integer votes,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.goals = goals;
        this.requiredSkills = requiredSkills;
        this.status = status;
        this.creatorId = creatorId;
        this.creatorUsername = creatorUsername;
        this.collaborators = collaborators;
        this.votes = votes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public List<String> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<String> collaborators) {
        this.collaborators = collaborators;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}