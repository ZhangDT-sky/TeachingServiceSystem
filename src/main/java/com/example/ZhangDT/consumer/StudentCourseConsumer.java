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

    /**
     * MQ 选课消费者异步落库实锤流水线：
     * 1. 全局防重锁 (Idempotency)：依凭 `msg_consumed:msgId` 的存在性鉴别纯 MQ 波动投递，如果遇上直接丢弃 Ack，绝不去碰库存。
     * 2. 漏网排异：走一次底表 `exists`，一旦命中代表存在“缓存意外失效的穿透新请求”，由于其又在预处理瞎扣了 1 票名额，系统对其执行补偿放生（`increment`+1名额还库）再抛弃。
     * 3. 落地入册：确认无虞，调用 MyBatis 刻下正式表单 `insert`。
     * 4. 并发深层结界：就算有外挂以毫秒差撞开了前卫漏过 `exists`，MySQL 表索引的硬隔离也会把这个双胞胎挤作 `DataIntegrityViolationException`。捕获这个报错后，明白这占坑无效，系统随即顺水推舟把该次扣下的预留名额再还给票池！
     * 5. 结案封印：确认完工落户抑或退还容错后，给该单号印上寿命并向 MQ 发达 BasicAck 断后。
     */
    @RabbitListener(queues = {"course.select.queue0","course.select.queue1","course.select.queue2"})
    public void selectCourse(Map<String,Object> msg, com.rabbitmq.client.Channel channel, 
                             @org.springframework.messaging.handler.annotation.Header(org.springframework.amqp.support.AmqpHeaders.DELIVERY_TAG) long tag){
        String msgId = msg.get("msgId") != null ? msg.get("msgId").toString() : null;
        try {
            String studentId = msg.get("studentId").toString();
            Integer courseId = Integer.valueOf(msg.get("courseId").toString());
            String semesterYear = msg.get("semesterYear") != null ? msg.get("semesterYear").toString() : null;
            Integer semesterTime = msg.get("semesterTime") != null ? Integer.valueOf(msg.get("semesterTime").toString()) : null;
            
            // 幂等去重：检查此 msgId 是否已被成功消费过 (针对 MQ 重试投递)
            if (msgId != null && Boolean.TRUE.equals(redisTemplate.hasKey("msg_consumed:" + msgId))) {
                logger.info("MQ重试重复消息拦截 (已消费过): msgId={}, studentId={}, courseId={}", msgId, studentId, courseId);
                channel.basicAck(tag, false);
                return;
            }

            if(studentCourseMapper.exists(studentId, courseId)) {
                // 能走到这里说明是新的 msgId (代表是一个绕过 Redis SISMEMBER 缓存的新 HTTP 请求)
                // 因为新的请求在 Lua 里又扣除了一次库存，所以我们必须要在这里退还库存
                redisTemplate.opsForValue().increment("course:" + courseId + ":capacity", 1);
                logger.info("重复选课拦截 (请求穿透缓存，已补偿库存退还): studentId={}, courseId={}, msgId={}", studentId, courseId, msgId);
                if (msgId != null) {
                    redisTemplate.opsForValue().set("msg_consumed:" + msgId, "1", 24, java.util.concurrent.TimeUnit.HOURS);
                }
                channel.basicAck(tag, false);
                return;
            }
            
            StudentCourse sc = new StudentCourse();
            sc.setStudentId(studentId);
            sc.setCourseId(courseId);
            sc.setSemesterYear(semesterYear);
            sc.setSemesterTime(semesterTime);
            studentCourseMapper.insert(sc);
            redisTemplate.opsForSet().add("student:" + studentId + ":courses",courseId.toString());
            
            // 记录消费成功
            if (msgId != null) {
                redisTemplate.opsForValue().set("msg_consumed:" + msgId, "1", 24, java.util.concurrent.TimeUnit.HOURS);
            }
            logger.info("选课成功: studentId={}, courseId={}", studentId, courseId);
            channel.basicAck(tag, false);
        } catch (org.springframework.dao.DataIntegrityViolationException dke) {
            // 包括 DuplicateKeyException。在此捕获说明并发情况下另一个线程刚完成了 insert
            // 当前线程被唯一定义或主键拦截了，说明该学生也是已经选过课的，退还库存
            redisTemplate.opsForValue().increment("course:" + msg.get("courseId").toString() + ":capacity", 1);
            logger.info("并发重复选课拦截 (数据库约束，已补偿库存退还): studentId={}, courseId={}", msg.get("studentId"), msg.get("courseId"));
            if (msgId != null) {
                redisTemplate.opsForValue().set("msg_consumed:" + msgId, "1", 24, java.util.concurrent.TimeUnit.HOURS);
            }
            try {
                channel.basicAck(tag, false);
            } catch (Exception ex) {
                logger.error("Ack失败: ", ex);
            }
        } catch (Exception e) {
            logger.error("选课消费异常: {}", msg, e);
            try {
                // 加入重试次数限制，防止死循环
                if (msgId != null) {
                    Long retries = redisTemplate.opsForValue().increment("msg_retry:" + msgId, 1);
                    if (retries != null && retries > 3) {
                        logger.error("选课消息重试超过3次，进入死信丢弃: {}", msgId);
                        redisTemplate.expire("msg_retry:" + msgId, 1, java.util.concurrent.TimeUnit.HOURS);
                        channel.basicNack(tag, false, false); // requeue = false
                        return;
                    }
                }
                channel.basicNack(tag, false, true);
            } catch (java.io.IOException ioException) {
                logger.error("消息拒绝异常: {}", ioException.getMessage());
            }
        }
    }

    /**
     * MQ 退课消费者异步退库放量流水线：
     * 1. 第一级防重拦网：检测 `msg_consumed:`，杜绝因 MQ 迷茫期引发的炒冷饭导致多添量，挡之。
     * 2. 原理级物理出局：强压进底层实行 `delete` 行级硬删操作。
     * 3. 极严谨点算余额：严正监控数据库被拔除的真实战果 `affected`（受影响记录）。当且仅当确实验证了（>0）抹去了老用户位置时，才准许将其化为极其宝贵的 1 颗流转火种库存容量（`+1`），放入票池放归系统。
     * 4. 打扫录事：做单据登记完工印签，回应 BasicAck 彻毕全单作业。
     */
    @RabbitListener(queues = {"course.drop.queue"})
    public void dropCourse(Map<String,Object> msg, com.rabbitmq.client.Channel channel,
                           @org.springframework.messaging.handler.annotation.Header(org.springframework.amqp.support.AmqpHeaders.DELIVERY_TAG) long tag){
        String msgId = msg.get("msgId") != null ? msg.get("msgId").toString() : null;
        try {
            String studentId = msg.get("studentId").toString();
            Integer courseId = Integer.valueOf(msg.get("courseId").toString());
            
            // 幂等去重
            if (msgId != null && Boolean.TRUE.equals(redisTemplate.hasKey("msg_consumed:" + msgId))) {
                logger.info("MQ重试退课重复消息拦截 (退课已处理过): msgId={}, studentId={}, courseId={}", msgId, studentId, courseId);
                channel.basicAck(tag, false);
                return;
            }

            int affected = 0;
            // 实际上这里的 exists 检查是防卫性的，可以直接进行 deleteByStudentIdAndCourseId 然后判断 affected
            if(studentCourseMapper.exists(studentId, courseId)) {
                affected = studentCourseMapper.deleteByStudentIdAndCourseId(studentId, courseId);
            }
            
            if (affected > 0) {
                // 仅当真实从数据库中删除了记录，才恢复容量！解决重试和重复发送无端放大容量的问题。
                redisTemplate.opsForValue().increment("course:" + courseId + ":capacity", 1);
                redisTemplate.opsForSet().remove("student:"+studentId+":courses", courseId.toString());
                logger.info("退课处理: studentId={}, courseId={}, 删除记录数={}, 成功恢复库存", studentId, courseId, affected);
            } else {
                logger.warn("退课处理无记录(或已被退): studentId={}, courseId={}", studentId, courseId);
            }
            
            if (msgId != null) {
                redisTemplate.opsForValue().set("msg_consumed:" + msgId, "1", 24, java.util.concurrent.TimeUnit.HOURS);
            }
            channel.basicAck(tag, false);
        } catch (Exception e) {
            logger.error("退课消费异常: {}", msg, e);
            try {
                // 加入重试次数限制
                if (msgId != null) {
                    Long retries = redisTemplate.opsForValue().increment("msg_retry:" + msgId, 1);
                    if (retries != null && retries > 3) {
                        logger.error("退课消息重试超过3次，进入死信丢弃: {}", msgId);
                        redisTemplate.expire("msg_retry:" + msgId, 1, java.util.concurrent.TimeUnit.HOURS);
                        channel.basicNack(tag, false, false); // requeue = false
                        return;
                    }
                }
                channel.basicNack(tag, false, true);
            } catch (java.io.IOException ioException) {
                logger.error("消息拒绝异常: {}", ioException.getMessage());
            }
        }
    }
}
