package com.nezhub.app.infrastructure.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                )
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put(CacheNames.TRENDING_PROJECTS,
                defaultConfig.entryTtl(Duration.ofHours(1))
        );

        cacheConfigurations.put(CacheNames.SEARCH_BY_SKILL,
                defaultConfig.entryTtl(Duration.ofMinutes(30))
        );

        cacheConfigurations.put(CacheNames.PROJECT_DETAILS,
                defaultConfig.entryTtl(Duration.ofHours(1))
        );

        cacheConfigurations.put(CacheNames.SKILL_STATS,
                defaultConfig.entryTtl(Duration.ofHours(2))
        );

        cacheConfigurations.put(CacheNames.STATUS_STATS,
                defaultConfig.entryTtl(Duration.ofHours(2))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    public static class CacheNames {
        public static final String TRENDING_PROJECTS = "trendingProjects";
        public static final String SEARCH_BY_SKILL = "searchBySkill";
        public static final String PROJECT_DETAILS = "projectDetails";
        public static final String SKILL_STATS = "skillStats";
        public static final String STATUS_STATS = "statusStats";
    }
}
