package com.example.ZhangDT.aspect;

import com.example.ZhangDT.annotation.RateLimit;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.ratelimit.RateLimitRule;
import com.example.ZhangDT.service.ratelimit.RateLimitRuleResolver;
import com.example.ZhangDT.service.ratelimit.RateLimiterService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Aspect
@Component
public class RateLimitAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private RateLimitRuleResolver rateLimitRuleResolver;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        String keyPrefix = rateLimit.key();
        String identity = resolveIdentity(signature.getParameterNames(), point.getArgs());
        String key = buildRateLimitKey(keyPrefix, className, methodName, identity);

        RateLimitRule rule = rateLimitRuleResolver.resolve(className, methodName, keyPrefix, rateLimit);

        try {
            boolean allowed = rateLimiterService.tryAcquire(
                    key,
                    rule.getRequestedTokens(),
                    rule.getReplenishRate(),
                    rule.getBurstCapacity()
            );

            if (allowed) {
                return point.proceed();
            }

            logger.warn("请求触发限流，Key={}, rule=[replenishRate={}, burstCapacity={}, requestedTokens={}]", key,
                    rule.getReplenishRate(), rule.getBurstCapacity(), rule.getRequestedTokens());
            return new ResponseMessage<>(429, "请求过于频繁，请稍后再试", null);
        } catch (Exception e) {
            logger.error("限流执行异常，已降级放行，Key={}", key, e);
            return point.proceed();
        }
    }

    private String buildRateLimitKey(String keyPrefix, String className, String methodName, String identity) {
        String prefix = keyPrefix == null ? "ratelimit:" : keyPrefix;
        String fixedPrefix = prefix.endsWith(":") ? prefix : prefix + ":";
        String safeIdentity = (identity == null || identity.isBlank()) ? "anonymous" : identity;
        return fixedPrefix + className + ":" + methodName + ":" + safeIdentity;
    }

    private String resolveIdentity(String[] parameterNames, Object[] args) {
        String identity = findIdentityFromNamedArgs(parameterNames, args);
        if (identity != null) {
            return identity;
        }

        identity = findIdentityFromObjectGetter(args);
        if (identity != null) {
            return identity;
        }

        identity = findIdentityFromObjectField(args);
        if (identity != null) {
            return identity;
        }

        return extractClientIpOrDefault();
    }

    private String findIdentityFromNamedArgs(String[] parameterNames, Object[] args) {
        if (parameterNames == null || args == null) {
            return null;
        }
        int size = Math.min(parameterNames.length, args.length);
        for (int i = 0; i < size; i++) {
            String name = parameterNames[i];
            Object value = args[i];
            if (value == null || name == null) {
                continue;
            }
            String lower = name.toLowerCase();
            if ("studentid".equals(lower) || "userid".equals(lower) || "uid".equals(lower)) {
                return String.valueOf(value);
            }
        }
        return null;
    }

    private String findIdentityFromObjectGetter(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            String byStudentId = invokeGetterIfPresent(arg, "getStudentId");
            if (byStudentId != null) {
                return byStudentId;
            }
            String byUserId = invokeGetterIfPresent(arg, "getUserId");
            if (byUserId != null) {
                return byUserId;
            }
        }
        return null;
    }

    private String invokeGetterIfPresent(Object target, String methodName) {
        try {
            Method getter = target.getClass().getMethod(methodName);
            Object value = getter.invoke(target);
            return value == null ? null : String.valueOf(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String findIdentityFromObjectField(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            String byStudentId = readFieldIfPresent(arg, "studentId");
            if (byStudentId != null) {
                return byStudentId;
            }
            String byUserId = readFieldIfPresent(arg, "userId");
            if (byUserId != null) {
                return byUserId;
            }
        }
        return null;
    }

    private String readFieldIfPresent(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(target);
            return value == null ? null : String.valueOf(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String extractClientIpOrDefault() {
        try {
            jakarta.servlet.http.HttpServletRequest request =
                    ((org.springframework.web.context.request.ServletRequestAttributes)
                            org.springframework.web.context.request.RequestContextHolder.getRequestAttributes()).getRequest();
            if (request == null) {
                return "default";
            }
            String xff = request.getHeader("X-Forwarded-For");
            if (xff != null && !xff.isBlank()) {
                return xff.split(",")[0].trim();
            }
            String realIp = request.getHeader("X-Real-IP");
            if (realIp != null && !realIp.isBlank()) {
                return realIp.trim();
            }
            String remoteAddr = request.getRemoteAddr();
            return (remoteAddr == null || remoteAddr.isBlank()) ? "default" : remoteAddr;
        } catch (Exception ignored) {
            return "default";
        }
    }
}
