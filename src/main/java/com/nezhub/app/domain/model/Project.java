package com.nezhub.app.domain.model;


import com.nezhub.app.domain.enums.ProjectStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "projects")
@CompoundIndexes({
        @CompoundIndex(name = "skill_status_votes_idx", def = "{'requiredSkills': 1, 'status': 1, 'votes': -1}"),
        @CompoundIndex(name = "status_votes_idx", def = "{'status': 1, 'votes': -1}")
})
public class Project {
    @Id
    private String id;
    private String title;

    private String description;

    private List<String> goals = new ArrayList<>();

    @Indexed
    private List<String> requiredSkills = new ArrayList<>();

    @Indexed
    private ProjectStatus status = ProjectStatus.OPEN;

    @Indexed
    private String creatorId;

    private List<String> collaborators = new ArrayList<>();

    private Integer votes = 0;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /* TODO: Cambiar al patrón builder */
    public Project(){}

    public Project(String id, String title, String description, List<String> goals,
                   List<String> requiredSkills, ProjectStatus status, String creatorId,
                   List<String> collaborators, Integer votes, LocalDateTime createdAt,
                   LocalDateTime updatedAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.goals = goals;
        this.requiredSkills = requiredSkills;
        this.status = status;
        this.creatorId = creatorId;
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
