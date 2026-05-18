package com.efeerturk.smart_ticket.redis.service.impl;

import com.efeerturk.smart_ticket.redis.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.info("Successfully cached data for key: {}", key);
        } catch (Exception e) {
            log.error("Error occurred while caching data for key: {}. Error: {}", key, e.getMessage());
        }
    }

    @Override
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Error occurred while fetching data from cache for key: {}. Error: {}", key, e.getMessage());
            return null;
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.info("Successfully deleted cached data for key: {}", key);
        } catch (Exception e) {
            log.error("Error occurred while deleting cached data for key: {}. Error: {}", key, e.getMessage());
        }
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}