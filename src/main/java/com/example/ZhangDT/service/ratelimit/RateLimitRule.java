package com.example.ZhangDT.service.ratelimit;

import com.example.ZhangDT.annotation.RateLimit;

public class RateLimitRule {
    private final int replenishRate;
    private final int burstCapacity;
    private final int requestedTokens;

    public RateLimitRule(int replenishRate, int burstCapacity, int requestedTokens) {
        this.replenishRate = replenishRate;
        this.burstCapacity = burstCapacity;
        this.requestedTokens = requestedTokens;
    }

    public int getReplenishRate() {
        return replenishRate;
    }

    public int getBurstCapacity() {
        return burstCapacity;
    }

    public int getRequestedTokens() {
        return requestedTokens;
    }

    public static RateLimitRule fromAnnotation(RateLimit rateLimit) {
        return new RateLimitRule(
                rateLimit.replenishRate(),
                rateLimit.burstCapacity(),
                rateLimit.requestedTokens()
        );
    }
}
