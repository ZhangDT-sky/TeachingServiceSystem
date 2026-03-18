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
        String type = msg.get("type") != null ? msg.get("type").toString() : "select";
        
        if ("select".equals(type)) {
            handleSelect(msg);
        } else if ("drop".equals(type)) {
            handleDrop(msg);
        }
    }

    private void handleSelect(Map<String, Object> msg) {
        try {
            String studentId = msg.get("studentId").toString();
            Integer courseId = Integer.valueOf(msg.get("courseId").toString());
            String semesterYear = msg.get("semesterYear") != null ? msg.get("semesterYear").toString() : null;
            Integer semesterTime = msg.get("semesterTime") != null ? Integer.valueOf(msg.get("semesterTime").toString()) : null;

            // 幂等性校验
            if (studentCourseMapper.exists(studentId, courseId)) {
                logger.info("[RocketMQ] 重复选课拦截: studentId={}, courseId={}", studentId, courseId);
                return;
            }

            StudentCourse sc = new StudentCourse();
            sc.setStudentId(studentId);
            sc.setCourseId(courseId);
            sc.setSemesterYear(semesterYear);
            sc.setSemesterTime(semesterTime);
            
            studentCourseMapper.insert(sc);
            redisTemplate.opsForSet().add("student:" + studentId + ":courses", courseId.toString());

            logger.info("[RocketMQ] 选课成功: studentId={}, courseId={}", studentId, courseId);
        } catch (Exception e) {
            logger.error("[RocketMQ] 选课执行异常: {}", msg, e);
            // RocketMQ 默认重试机制会介入
            throw new RuntimeException("选课执行失败", e);
        }
    }

    private void handleDrop(Map<String, Object> msg) {
        try {
            String studentId = msg.get("studentId").toString();
            Integer courseId = Integer.valueOf(msg.get("courseId").toString());
            
            int affected = 0;
            if (studentCourseMapper.exists(studentId, courseId)) {
                affected = studentCourseMapper.deleteByStudentIdAndCourseId(studentId, courseId);
            }
            
            redisTemplate.opsForValue().increment("course:" + courseId + ":capacity", 1);
            redisTemplate.opsForSet().remove("student:" + studentId + ":courses", courseId.toString());
            
            logger.info("[RocketMQ] 退课处理完毕: studentId={}, courseId={}, 删除记录数={}", studentId, courseId, affected);
        } catch (Exception e) {
            logger.error("[RocketMQ] 退课执行异常: {}", msg, e);
            throw new RuntimeException("退课处理失败", e);
        }
    }
}
