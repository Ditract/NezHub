package com.nezhub.app.domain.repository;

import com.nezhub.app.domain.enums.ProjectStatus;
import com.nezhub.app.domain.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByRequiredSkillsContaining(String skill);

    List<Project> findByRequiredSkillsContainingAndStatus(String skill, ProjectStatus status);

    List<Project> findByCreatorId(String creatorId);

    List<Project> findByStatusOrderByVotesDesc(ProjectStatus status);

    Page<Project> findByStatusOrderByVotesDesc(ProjectStatus status, Pageable pageable);

    List<Project> findByCreatorIdAndStatus(String creatorId, ProjectStatus status);
}
