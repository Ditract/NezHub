package com.nezhub.app.domain.repository;

import com.nezhub.app.domain.enums.CollaborationStatus;
import com.nezhub.app.domain.model.Collaboration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollaborationRepository extends MongoRepository<Collaboration, String> {

    List<Collaboration> findByProjectIdAndStatus(String projectId, CollaborationStatus status);

    Optional<Collaboration> findByProjectIdAndUserId(String projectId, String userId);

    Optional<Collaboration> findByProjectIdAndUserIdAndStatus(
            String projectId,
            String userId,
            CollaborationStatus status
    );

    List<Collaboration> findByUserId(String userId);

    List<Collaboration> findByUserIdAndStatus(String userId, CollaborationStatus status);

    long countByProjectIdAndStatus(String projectId, CollaborationStatus status);

    boolean existsByProjectIdAndUserIdAndStatus(
            String projectId,
            String userId,
            CollaborationStatus status
    );

    List<Collaboration> findByProjectId(String projectId);
}
