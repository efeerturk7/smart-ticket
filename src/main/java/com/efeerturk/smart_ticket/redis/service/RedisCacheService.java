package com.efeerturk.smart_ticket.redis.service;

import java.util.concurrent.TimeUnit;

public interface RedisCacheService {
    void set(String key, Object value, long timeout, TimeUnit unit);
    Object get(String key);
    void delete(String key);
    boolean exists(String key);
}
