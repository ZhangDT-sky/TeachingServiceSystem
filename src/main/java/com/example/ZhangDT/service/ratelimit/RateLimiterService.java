package com.example.ZhangDT.service.ratelimit;

public interface RateLimiterService {
    boolean tryAcquire(String key, int permits, int replenishRate, int burstCapacity);
}
