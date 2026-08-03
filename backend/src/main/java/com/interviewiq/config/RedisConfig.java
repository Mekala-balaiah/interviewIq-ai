package com.interviewiq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import java.util.Map;

/**
 * Redis configuration for caching and distributed data.
 *
 * <p>Cache regions and their TTLs:
 * <ul>
 *   <li>{@code user-profiles}       — 30 min
 *   <li>{@code candidate-profiles}  — 30 min
 *   <li>{@code dashboard-kpis}      — 5 min
 *   <li>{@code ats-scores}          — 60 min
 *   <li>{@code ai-responses}        — 24 hours
 *   <li>{@code job-candidates}      — 15 min
 *   <li>{@code skills}              — 24 hours
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues()
                .prefixCacheNameWith("interviewiq:");

        // Per-cache TTL overrides
        Map<String, RedisCacheConfiguration> cacheConfigs = Map.of(
                "user-profiles",        defaultConfig.entryTtl(Duration.ofMinutes(30)),
                "candidate-profiles",   defaultConfig.entryTtl(Duration.ofMinutes(30)),
                "dashboard-kpis",       defaultConfig.entryTtl(Duration.ofMinutes(5)),
                "ats-scores",           defaultConfig.entryTtl(Duration.ofHours(1)),
                "ai-responses",         defaultConfig.entryTtl(Duration.ofHours(24)),
                "job-candidates-ranked",defaultConfig.entryTtl(Duration.ofMinutes(15)),
                "skills",               defaultConfig.entryTtl(Duration.ofHours(24)),
                "job-details",          defaultConfig.entryTtl(Duration.ofMinutes(10))
        );

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}
