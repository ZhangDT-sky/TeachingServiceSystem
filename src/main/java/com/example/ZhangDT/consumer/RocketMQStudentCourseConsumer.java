package com.example.ZhangDT.consumer;

import com.example.ZhangDT.bean.StudentCourse;
import com.example.ZhangDT.mapper.StudentCourseMapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * RocketMQ 消费者版本
 * 监听 course-selection-topic
 */
@Component
@RocketMQMessageListener(topic = "course-selection-topic", consumerGroup = "course-consumer-group")
public class RocketMQStudentCourseConsumer implements RocketMQListener<Map<String, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQStudentCourseConsumer.class);

    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(Map<String, Object> msg) {
        String msgId = msg.get("msgId") != null ? msg.get("msgId").toString() : null;
        String type = msg.get("type") != null ? msg.get("type").toString() : "select";
        
        // 幂等去重：检查此 msgId 是否已被成功消费过
        if (msgId != null && Boolean.TRUE.equals(redisTemplate.hasKey("msg_consumed:" + msgId))) {
            logger.info("[RocketMQ] 重复消息拦截 (已消费过): msgId={}, type={}", msgId, type);
            return;
        }

        try {
            if ("select".equals(type)) {
                handleSelect(msg, msgId);
            } else if ("drop".equals(type)) {
                handleDrop(msg, msgId);
            }
            
            // 记录消费成功
            if (msgId != null) {
                redisTemplate.opsForValue().set("msg_consumed:" + msgId, "1", 24, java.util.concurrent.TimeUnit.HOURS);
            }
        } catch (Exception e) {
            logger.error("[RocketMQ] 消费异常: type={}, msgId={}", type, msgId, e);
            // 处理重试逻辑
            if (msgId != null) {
                Long retries = redisTemplate.opsForValue().increment("msg_retry:" + msgId, 1);
                if (retries != null && retries > 3) {
                    logger.error("[RocketMQ] 消息重试超过3次，丢弃: {}", msgId);
                    redisTemplate.expire("msg_retry:" + msgId, 1, java.util.concurrent.TimeUnit.HOURS);
                    return; // 不再抛异常，等同于 Ack
                }
            }
            throw new RuntimeException("RocketMQ 消费失败，触发重试", e);
        }
    }

    private void handleSelect(Map<String, Object> msg, String msgId) {
        String studentId = msg.get("studentId").toString();
        Integer courseId = Integer.valueOf(msg.get("courseId").toString());
        String semesterYear = msg.get("semesterYear") != null ? msg.get("semesterYear").toString() : null;
        Integer semesterTime = msg.get("semesterTime") != null ? Integer.valueOf(msg.get("semesterTime").toString()) : null;

        if (studentCourseMapper.exists(studentId, courseId)) {
            // 穿透补偿：由于 Lua 脚本预扣了库存，此处发现已存在则回还
            redisTemplate.opsForValue().increment("course:" + courseId + ":capacity", 1);
            logger.info("[RocketMQ] 重复选课拦截 (补偿退还库存): studentId={}, courseId={}", studentId, courseId);
            return;
        }

        StudentCourse sc = new StudentCourse();
        sc.setStudentId(studentId);
        sc.setCourseId(courseId);
        sc.setSemesterYear(semesterYear);
        sc.setSemesterTime(semesterTime);
        
        try {
            studentCourseMapper.insert(sc);
            redisTemplate.opsForSet().add("student:" + studentId + ":courses", courseId.toString());
            logger.info("[RocketMQ] 选课成功: studentId={}, courseId={}", studentId, courseId);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 并发冲突补偿
            redisTemplate.opsForValue().increment("course:" + courseId + ":capacity", 1);
            logger.info("[RocketMQ] 并发重复选课碰撞 (数据库拦截，补偿退还库存): studentId={}, courseId={}", studentId, courseId);
        }
    }

    private void handleDrop(Map<String, Object> msg, String msgId) {
        String studentId = msg.get("studentId").toString();
        Integer courseId = Integer.valueOf(msg.get("courseId").toString());
        
        int affected = 0;
        if (studentCourseMapper.exists(studentId, courseId)) {
            affected = studentCourseMapper.deleteByStudentIdAndCourseId(studentId, courseId);
        }
        
        if (affected > 0) {
            redisTemplate.opsForValue().increment("course:" + courseId + ":capacity", 1);
            redisTemplate.opsForSet().remove("student:" + studentId + ":courses", courseId.toString());
            logger.info("[RocketMQ] 退课处理完成: studentId={}, courseId={}, 恢复库存", studentId, courseId);
        } else {
            logger.warn("[RocketMQ] 退课处理无记录: studentId={}, courseId={}", studentId, courseId);
        }
    }
}
