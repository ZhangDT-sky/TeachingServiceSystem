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

}
