package com.example.ZhangDT.consumer;

import com.example.ZhangDT.bean.StudentCourse;
import com.example.ZhangDT.mapper.StudentCourseMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Component
public class StudentCourseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(StudentCourseConsumer.class);

    @Autowired
    private StudentCourseMapper studentCourseMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @RabbitListener(queues = {"course.select.queue0","course.select.queue1","course.select.queue2"})
    public void selectCourse(Map<String,Object> msg){
        try {
            String studentId = msg.get("studentId").toString();
            Integer courseId = Integer.valueOf(msg.get("courseId").toString());
            String semesterYear = msg.get("semesterYear") != null ? msg.get("semesterYear").toString() : null;
            Integer semesterTime = msg.get("semesterTime") != null ? Integer.valueOf(msg.get("semesterTime").toString()) : null;
            if(studentCourseMapper.exists(studentId, courseId)) {
                redisTemplate.opsForValue().increment("course:" + courseId + ":capacity", 1);
                logger.info("重复选课拦截: studentId={}, courseId={}", studentId, courseId);
                return;
            }
            StudentCourse sc = new StudentCourse();
            sc.setStudentId(studentId);
            sc.setCourseId(courseId);
            sc.setSemesterYear(semesterYear);
            sc.setSemesterTime(semesterTime);
            studentCourseMapper.insert(sc);
            redisTemplate.opsForSet().add("student:" + studentId + ":courses",courseId.toString());
            logger.info("选课成功: studentId={}, courseId={}", studentId, courseId);
        } catch (Exception e) {
            Integer courseId = Integer.valueOf(msg.get("courseId").toString());
            redisTemplate.opsForValue().increment("course:" + courseId + ":capacity", 1);
            logger.error("选课消费异常: {}", msg, e);
        }
    }

    @RabbitListener(queues = {"course.drop.queue"})
    public void dropCourse(Map<String,Object> msg){
        try {
            String studentId = msg.get("studentId").toString();
            Integer courseId = Integer.valueOf(msg.get("courseId").toString());
            int affected = 0;
            if(studentCourseMapper.exists(studentId, courseId)) {
                affected = studentCourseMapper.deleteByStudentIdAndCourseId(studentId, courseId);
            }
            redisTemplate.opsForValue().increment("course:" + courseId + ":capacity", 1);
            redisTemplate.opsForSet().remove("student:"+studentId+":courses", courseId.toString());
            logger.info("退课处理: studentId={}, courseId={}, 删除记录数={}", studentId, courseId, affected);
        } catch (Exception e) {
            logger.error("退课消费异常: {}", msg, e);
        }
    }
}
