package com.example.ZhangDT.annotation;

import java.lang.annotation.*;

/**
 * 自定义令牌桶限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流资源的 Key 前缀
     */
    String key() default "ratelimit:";

    /**
     * 每秒补充令牌的速度 (生成速率)
     */
    int replenishRate() default 1;

    /**
     * 令牌桶的最大容量 (允许的突发流量)
     */
    int burstCapacity() default 2;

    /**
     * 请求需要消耗的令牌数量，默认为 1
     */
    int requestedTokens() default 1;
}
