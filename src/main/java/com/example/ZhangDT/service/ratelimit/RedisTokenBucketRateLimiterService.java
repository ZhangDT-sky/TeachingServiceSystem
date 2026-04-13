package com.example.ZhangDT.service.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

@Service
public class RedisTokenBucketRateLimiterService implements RateLimiterService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisScript<Long> rateLimitScript;

    @Override
    public boolean tryAcquire(String key, int permits, int replenishRate, int burstCapacity) {
        long now = Instant.now().getEpochSecond();
        Long result = redisTemplate.execute(
                rateLimitScript,
                Collections.singletonList(key),
                String.valueOf(replenishRate),
                String.valueOf(burstCapacity),
                String.valueOf(permits),
                String.valueOf(now)
        );
        return result != null && result == 1L;
    }
}
