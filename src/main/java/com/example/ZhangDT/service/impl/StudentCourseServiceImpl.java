package com.example.ZhangDT.service.impl;

import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.StudentCourseService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class StudentCourseServiceImpl implements StudentCourseService {

    private static final Logger logger = LoggerFactory.getLogger(StudentCourseServiceImpl.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisScript<Long> selectCourseScript;

    @Autowired
    private RedisScript<Long> dropCourseScript;

    @Autowired
    private RedisScript<Long> seqCourseScript;

    @Override
    public ResponseMessage<String> selectCourse(String studentId, Integer courseId, String semesterYear, String semesterTime) {
        try{
            List<String> keys = Arrays.asList(
                    "student:"+studentId+":courses",
                    "course:"+courseId+":capacity"
            );

            Long result = redisTemplate.execute(
                    selectCourseScript,
                    keys,
                    courseId.toString());

            if(result == null){
                logger.info("redis 脚本执行失败");
                throw new RuntimeException("redis 脚本执行失败");
            }
            switch (result.intValue()){
                case 0: // 已选过课程
                    logger.info("重复选课拦截: studentId={}, courseId={}", studentId, courseId);
                    return ResponseMessage.fail("已进行过选课操作");
                case 1: // 课程已满
                    logger.info("课程已满: studentId={}, courseId={}", studentId, courseId);
                    return ResponseMessage.fail("课程已满");
                case 2: // 选课成功
//                     发送异步消息处理后续逻辑
                    Map<String, Object> msg = new HashMap<>();
                    msg.put("studentId", studentId);
                    msg.put("courseId", courseId);
                    msg.put("semesterYear", semesterYear);
                    msg.put("semesterTime", semesterTime);
                    msg.put("type", "select");

                    Long seq = redisTemplate.execute(
                            seqCourseScript,
                            Collections.singletonList("course.select.seq"),
                            String.valueOf(3));
                    String queue = "course.select.queue" + seq;
                    rabbitTemplate.convertAndSend(queue, msg);
                    logger.info("选课请求已提交: studentId={}, courseId={}, queue={}", studentId, courseId, queue);
                    return ResponseMessage.success("选课请求已提交，结果稍后可查");
                default:
                    throw new RuntimeException("未知的脚本返回值: " + result);


            }

        }
        catch (Exception e){
            logger.error("选课异常: studentId={}, courseId={}", studentId, courseId, e);
            return ResponseMessage.fail("系统异常，请稍后重试");
        }
    }

    @Override
    public ResponseMessage<String> dropCourse(String studentId, Integer courseId, String semesterYear, String semesterTime) {
        try{
            //准备Redis键
            List<String> keys = Arrays.asList(
                    "student:"+studentId+":courses",
                    "course:"+courseId+":capacity"
            );
            // 执行原子化退课操作
            Long result = redisTemplate.execute(
                    dropCourseScript,
                    keys,
                    courseId.toString()
            );

            if(result == null){
                logger.info("redis 脚本执行失败");
                throw new RuntimeException("redis 脚本执行失败");
            }
            switch (result.intValue()){
                case 0:
                    logger.info("退课失败-未选该课程: studentId={}, courseId={}", studentId, courseId);
                    return ResponseMessage.fail("未选该课程，无法退课");
                case 1:// 退课成功
                    Map<String, Object> msg = new HashMap<>();
                    msg.put("studentId", studentId);
                    msg.put("courseId", courseId);
                    msg.put("semesterYear", semesterYear);
                    msg.put("semesterTime", semesterTime);
                    msg.put("type", "drop");

                    String queue = "course.drop.queue";
                    rabbitTemplate.convertAndSend(queue, msg);

                    logger.info("退课请求已提交: studentId={}, courseId={}, queue={}", studentId, courseId, queue);
                    return ResponseMessage.success("退课请求已提交，结果稍后可查询");
                default:
                    throw new RuntimeException("未知的脚本返回值: " + result);
            }
        }
        catch (Exception e){
            logger.error("退课异常: studentId={}, courseId={}, error=", studentId, courseId, e);
            return ResponseMessage.fail("系统异常，请稍后重试");
        }
    }
}
