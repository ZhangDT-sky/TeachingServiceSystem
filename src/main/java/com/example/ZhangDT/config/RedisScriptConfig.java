package com.example.ZhangDT.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisScriptConfig {

    @Bean
    public RedisScript<Long> selectCourseScript(){
        String script=
                "local studentKey = KEYS[1]\n" +
                "local capacityKey = KEYS[2]\n"+
                "local courseId = ARGV[1]\n"+
                "\n"+
                "if redis.call('SISMEMBER', studentKey, courseId) == 1 then\n" +
                "    return 0\n" +  // 已选过课程
                "end\n"+
                "\n"+
                "local remain = tonumber(redis.call('GET', capacityKey))\n" +
                "if not remain or remain <= 0 then\n" +
                "    return 1\n" +  // 课程已满
                "end\n" +
                "\n" +
                "redis.call('DECR', capacityKey)\n" +
                "redis.call('SADD', studentKey, courseId)\n" +
                "return 2";  // 选课成 功
        return RedisScript.of(script, Long.class);
    }

    @Bean
    public RedisScript<Long> dropCourseScript() {
        String script =
                "local studentKey = KEYS[1]\n" +
                        "local capacityKey = KEYS[2]\n" +
                        "local courseId = ARGV[1]\n" +
                        "\n" +
                        "if redis.call('SISMEMBER', studentKey, courseId) == 0 then\n" +
                        "    return 0\n" +  // 未选课程
                        "end\n" +
                        "\n" +
                        "redis.call('SREM', studentKey, courseId)\n" +
                        "redis.call('INCR', capacityKey)\n" +
                        "return 1";  // 退课成功

        return RedisScript.of(script, Long.class);
    }


    @Bean
    public RedisScript<Long> seqCourseScript(){
        String script="local val = redis.call('incr', KEYS[1]) " +
                      "local mod = tonumber(ARGV[1]) " +
                      "return (val - 1) % mod";
        return RedisScript.of(script, Long.class);
    }

    @Bean
    public RedisScript<Long> rateLimitScript() {
        String script = 
            "local key = KEYS[1]\n" +
            "local replenishRate = tonumber(ARGV[1])\n" +
            "local capacity = tonumber(ARGV[2])\n" +
            "local requested = tonumber(ARGV[3])\n" +
            "local now = tonumber(ARGV[4])\n" +
            "\n" +
            "local fill_time = capacity / replenishRate\n" +
            "local ttl = math.floor(fill_time * 2)\n" +
            "\n" +
            "local last_tokens = tonumber(redis.call('hget', key, 'tokens'))\n" +
            "if last_tokens == nil then\n" +
            "    last_tokens = capacity\n" +
            "end\n" +
            "\n" +
            "local last_refreshed = tonumber(redis.call('hget', key, 'timestamp'))\n" +
            "if last_refreshed == nil then\n" +
            "    last_refreshed = 0\n" +
            "end\n" +
            "\n" +
            "local delta = math.max(0, now - last_refreshed)\n" +
            "local filled_tokens = math.min(capacity, last_tokens + (delta * replenishRate))\n" +
            "local allowed = filled_tokens >= requested\n" +
            "local new_tokens = filled_tokens\n" +
            "if allowed then\n" +
            "    new_tokens = filled_tokens - requested\n" +
            "end\n" +
            "\n" +
            "redis.call('hset', key, 'tokens', new_tokens)\n" +
            "redis.call('hset', key, 'timestamp', now)\n" +
            "redis.call('expire', key, ttl)\n" +
            "if allowed then\n" +
            "    return 1\n" +
            "else\n" +
            "    return 0\n" +
            "end";
        return RedisScript.of(script, Long.class);
    }

}
