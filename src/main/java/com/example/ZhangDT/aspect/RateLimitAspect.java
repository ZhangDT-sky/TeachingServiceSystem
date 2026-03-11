package com.example.ZhangDT.aspect;

import com.example.ZhangDT.annotation.RateLimit;
import com.example.ZhangDT.core.ResponseMessage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Collections;

@Aspect
@Component
public class RateLimitAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedisScript<Long> rateLimitScript;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        String keyPrefix = rateLimit.key();
        int replenishRate = rateLimit.replenishRate();
        int burstCapacity = rateLimit.burstCapacity();
        int requestedTokens = rateLimit.requestedTokens();

        // 尝试从请求参数中获取 studentId 或其他标识构建唯一 Key
        String key = keyPrefix;
        Object[] args = point.getArgs();
        if (args != null && args.length > 0) {
            Object arg = args[0];
            try {
                Method getStudentIdMethod = arg.getClass().getMethod("getStudentId");
                Object studentIdObj = getStudentIdMethod.invoke(arg);
                if (studentIdObj != null) {
                    key = keyPrefix + studentIdObj.toString();
                }
            } catch (Exception e) {
                // 如果参数对象没有 getStudentId 方法，使用默认后缀
                key = keyPrefix + "default";
            }
        }

        long now = Instant.now().getEpochSecond();

        try {
            Long result = redisTemplate.execute(
                    rateLimitScript,
                    Collections.singletonList(key),
                    String.valueOf(replenishRate),
                    String.valueOf(burstCapacity),
                    String.valueOf(requestedTokens),
                    String.valueOf(now)
            );

            if (result != null && result == 1L) {
                // 令牌获取成功，放行
                return point.proceed();
            } else {
                logger.warn("请求触发限流，Key: {}", key);
                return ResponseMessage.fail("请求过于频繁，请稍后再试");
            }
        } catch (Exception e) {
            logger.error("限流脚本执行异常", e);
            // 发生异常时可以降级处理，此处选择放行以免阻断用户请求
            return point.proceed();
        }
    }
}
