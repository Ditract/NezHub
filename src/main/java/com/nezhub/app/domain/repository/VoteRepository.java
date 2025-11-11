package com.nezhub.app.domain.repository;

import com.nezhub.app.domain.model.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends MongoRepository<Vote, String> {

    /**
     * Busca voto de un usuario en un proyecto específico.
     */
    Optional<Vote> findByProjectIdAndUserId(String projectId, String userId);

    /**
     * Verifica si existe voto de usuario en proyecto.
     */
    boolean existsByProjectIdAndUserId(String projectId, String userId);

    /**
     * Elimina voto de usuario en proyecto.
     */
    void deleteByProjectIdAndUserId(String projectId, String userId);
}
