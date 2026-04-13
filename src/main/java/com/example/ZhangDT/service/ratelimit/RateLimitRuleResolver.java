package com.example.ZhangDT.service.ratelimit;

import com.example.ZhangDT.annotation.RateLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RateLimitRuleResolver {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitRuleResolver.class);

    private static final String METHOD_RULE_KEY_PREFIX = "ratelimit:rule:";
    private static final String PREFIX_RULE_KEY_PREFIX = "ratelimit:rule:prefix:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public RateLimitRule resolve(String className, String methodName, String keyPrefix, RateLimit annotationRule) {
        RateLimitRule defaultRule = RateLimitRule.fromAnnotation(annotationRule);

        // 1) 动态规则优先：方法级（支持 HASH 与 JSON 文本）
        String methodRuleKey = METHOD_RULE_KEY_PREFIX + className + ":" + methodName;
        RateLimitRule methodRule = loadRuleFromRedis(methodRuleKey, defaultRule);
        if (methodRule != null) {
            return methodRule;
        }

        // 2) 其次：业务前缀级（支持 HASH 与 JSON 文本）
        String normalizedPrefix = normalizePrefix(keyPrefix);
        String prefixRuleKey = PREFIX_RULE_KEY_PREFIX + normalizedPrefix;
        RateLimitRule prefixRule = loadRuleFromRedis(prefixRuleKey, defaultRule);
        if (prefixRule != null) {
            return prefixRule;
        }

        // 3) 最后：注解默认值
        return defaultRule;
    }

    private RateLimitRule loadRuleFromRedis(String key, RateLimitRule fallback) {
        try {
            Map<Object, Object> hash = redisTemplate.opsForHash().entries(key);
            if (hash != null && !hash.isEmpty()) {
                return fromMap(toStringMap(hash), fallback);
            }

            String json = redisTemplate.opsForValue().get(key);
            if (json != null && !json.isBlank()) {
                Map<String, String> parsed = parseSimpleJson(json);
                return fromMap(parsed, fallback);
            }
        } catch (Exception e) {
            logger.warn("加载限流动态规则失败，Key={}", key, e);
        }
        return null;
    }

    private RateLimitRule fromMap(Map<String, String> map, RateLimitRule fallback) {
        int replenishRate = parsePositiveInt(map.get("replenishRate"), fallback.getReplenishRate());
        int burstCapacity = parsePositiveInt(map.get("burstCapacity"), fallback.getBurstCapacity());
        int requestedTokens = parsePositiveInt(map.get("requestedTokens"), fallback.getRequestedTokens());
        return new RateLimitRule(replenishRate, burstCapacity, requestedTokens);
    }

    private int parsePositiveInt(String raw, int defaultValue) {
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            return value > 0 ? value : defaultValue;
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private Map<String, String> toStringMap(Map<Object, Object> source) {
        Map<String, String> target = new HashMap<>();
        for (Map.Entry<Object, Object> entry : source.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            target.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        return target;
    }

    // 轻量 JSON 解析，兼容 {"replenishRate":2,"burstCapacity":5,"requestedTokens":1}
    private Map<String, String> parseSimpleJson(String json) {
        Map<String, String> result = new HashMap<>();
        String trimmed = json.trim();
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            return result;
        }
        String body = trimmed.substring(1, trimmed.length() - 1).trim();
        if (body.isEmpty()) {
            return result;
        }

        String[] pairs = body.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length != 2) {
                continue;
            }
            String key = stripQuote(kv[0].trim());
            String value = stripQuote(kv[1].trim());
            result.put(key, value);
        }
        return result;
    }

    private String stripQuote(String raw) {
        String text = raw;
        if (text.startsWith("\"") && text.endsWith("\"") && text.length() >= 2) {
            text = text.substring(1, text.length() - 1);
        }
        return text;
    }

    private String normalizePrefix(String keyPrefix) {
        if (keyPrefix == null || keyPrefix.isBlank()) {
            return "default";
        }
        String normalized = keyPrefix;
        while (normalized.endsWith(":")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized.isBlank() ? "default" : normalized;
    }
}
