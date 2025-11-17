package com.nezhub.app.application.service;

import com.nezhub.app.domain.enums.ProjectStatus;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProjectStatisticsService {

    private final MongoTemplate mongoTemplate;

    public ProjectStatisticsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Obtiene las habilidades más populares (más usadas en proyectos).
     */
    @Cacheable(value = "skillStats", key = "#limit")
    public List<SkillStats> getMostPopularSkills(int limit) {
        UnwindOperation unwind = Aggregation.unwind("requiredSkills");

        GroupOperation group = Aggregation.group("requiredSkills")
                .count().as("count");

        SortOperation sort = Aggregation.sort(
                org.springframework.data.domain.Sort.Direction.DESC, "count"
        );

        Aggregation aggregation = Aggregation.newAggregation(
                unwind,
                group,
                sort,
                Aggregation.limit(limit)
        );

        AggregationResults<SkillStats> results = mongoTemplate.aggregate(
                aggregation,
                "projects",
                SkillStats.class
        );

        return results.getMappedResults();
    }

    /**
     * Cuenta proyectos por estado.
     */
    @Cacheable(value = "statusStats")
    public List<StatusStats> getProjectStatsByStatus() {
        GroupOperation group = Aggregation.group("status")
                .count().as("count");

        Aggregation aggregation = Aggregation.newAggregation(group);

        AggregationResults<StatusStats> results = mongoTemplate.aggregate(
                aggregation,
                "projects",
                StatusStats.class
        );

        return results.getMappedResults();
    }

    /**
     * DTOs para resultados de aggregations.
     */
    public static class SkillStats {
        private String _id;  // Skill name (del group by)
        private long count;  // Número de proyectos

        public String getSkill() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }

    public static class StatusStats {
        private String _id;
        private long count;

        public ProjectStatus getStatus() {
            return ProjectStatus.valueOf(_id);
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}

