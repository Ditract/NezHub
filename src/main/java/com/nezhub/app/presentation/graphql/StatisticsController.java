package com.nezhub.app.presentation.graphql;

import com.nezhub.app.application.service.ProjectStatisticsService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class StatisticsController {

    private final ProjectStatisticsService statisticsService;

    public StatisticsController(ProjectStatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * Query: Obtener habilidades más populares.
     */
    @QueryMapping
    public List<ProjectStatisticsService.SkillStats> getMostPopularSkills(
            @Argument(name = "limit") Integer limit
    ) {
        int actualLimit = (limit != null && limit > 0) ? limit : 10;
        return statisticsService.getMostPopularSkills(actualLimit);
    }

    /**
     * Query: Obtener estadísticas por estado.
     */
    @QueryMapping
    public List<ProjectStatisticsService.StatusStats> getProjectStatsByStatus() {
        return statisticsService.getProjectStatsByStatus();
    }
}
