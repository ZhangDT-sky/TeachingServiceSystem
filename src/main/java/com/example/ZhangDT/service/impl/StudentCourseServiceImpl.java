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

    @Autowired
    private org.apache.rocketmq.spring.core.RocketMQTemplate rocketMQTemplate;

    private static final String ROCKETMQ_TOPIC = "course-selection-topic";

    /**
     * 选课业务预处理与异步发车流程：
     * 1. 生成独一无二的 msgId：用于后续整个链路防重复监控。
     * 2. 执行原子化 Lua 脚本进行初步校验：
     *    - 拦截判重：检查集合中是否已有该学生（SISMEMBER），若是则返回 0 拒绝。
     *    - 拦截超卖：查验课程可分配容量（capacity），不足则返回 1 拒绝。
     *    - 预扣库存：校验通过后，进行原子的容量扣除（DECR）以及加入已选集合（SADD），返回 2 成功。
     * 3. 推入MQ顺序排队区：使用 studentId+courseId 作为分区 Key，利用 RocketMQ 顺序消息保证同一动作的绝对时序。
     * 4. 高可用兜底补偿：如果在发送 RocketMQ 瞬间因为网络等崩溃报错，Catch 区块会立刻逆向回退刚才 Lua 对 Redis 扣去的名额和登记，确保绝不少卖。
     */
    @Override
    public ResponseMessage<String> selectCourse(String studentId, Integer courseId, String semesterYear,
            String semesterTime) {
        try {
            List<String> keys = Arrays.asList(
                    "student:" + studentId + ":courses",
                    "course:" + courseId + ":capacity");

            Long result = redisTemplate.execute(
                    selectCourseScript,
                    keys,
                    courseId.toString());

            if (result == null) {
                logger.error("Redis 选课脚本执行返回空结果，可能由于连接超时或脚本环境异常: studentId={}, courseId={}", studentId, courseId);
                return ResponseMessage.fail("系统繁忙（Redis异常），请稍后重试");
            }
            switch (result.intValue()) {
                case 0: // 已选过课程
                    logger.info("重复选课拦截: studentId={}, courseId={}", studentId, courseId);
                    return ResponseMessage.fail("已进行过选课操作");
                case 1: // 课程已满
                    logger.info("课程已满: studentId={}, courseId={}", studentId, courseId);
                    return ResponseMessage.fail("课程已满");
                case 2: // 选课成功
                    // 发送异步消息处理后续逻辑
                    Map<String, Object> msg = new HashMap<>();
                    String selectMsgId = UUID.randomUUID().toString();
                    msg.put("msgId", selectMsgId);
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
                    // 主方案：RocketMQ 顺序消息发送
                    // 使用 studentId+courseId 作为分区 Key，保证同一学生对同一课程的操作
                    // (选课/退课) 被路由到同一个 Queue，消费端单线程顺序处理，彻底避免乱序
                    String orderKey = studentId + "-" + courseId;
                    try {
                        rocketMQTemplate.syncSendOrderly(
                                ROCKETMQ_TOPIC + ":select",
                                org.springframework.messaging.support.MessageBuilder.withPayload(msg).build(),
                                orderKey);
                        logger.info("选课请求已顺序提交 (RocketMQ): studentId={}, courseId={}, key={}, msgId={}", studentId, courseId, orderKey, selectMsgId);

                        // 备用方案：RabbitMQ (当前注释)
                        // rabbitTemplate.convertAndSend(queue, msg);

                        return ResponseMessage.success("选课请求已提交，结果稍后可查");
                    } catch (Exception e) {
                        logger.error("MQ发送异常，回滚选课Redis库存: studentId={}, courseId={}", studentId, courseId, e);
                        redisTemplate.opsForValue().increment("course:" + courseId + ":capacity", 1);
                        redisTemplate.opsForSet().remove("student:" + studentId + ":courses", courseId.toString());
                        return ResponseMessage.fail("系统繁忙，提交选课请求失败，请稍后重试");
                    }
                default:
                    throw new RuntimeException("未知的脚本返回值: " + result);

            }

        } catch (Exception e) {
            logger.error("选课异常: studentId={}, courseId={}", studentId, courseId, e);
            return ResponseMessage.fail("系统异常，请稍后重试");
        }
    }

    /**
     * 退课业务预处理与异步发车流程：
     * 1. 同理生成独一无二的单据 msgId 防止消费者重复接收执行。
     * 2. 执行原子化 Lua 退课脚本：
     *    - 只做限选核验（SISMEMBER 判断若未选课则直接返回 0）。
     *    - 通过后，仅负责将他移除排重合集（SREM），此时【绝对不立即增加库存 capacity】，将库存的主动权交给落库安全的消费者。
     * 3. 打包推入 RocketMQ 顺序队列：通过与选课一致的 Key 保证“选->退”动作被同一个 Consumer 线程串行处理，彻底消除竞争风险。
     * 4. 同等异常兜底：若投递该网络发生异常，由于 Lua 没有加量，所以只需要利用 SADD 强制把其状态恢复至已选即可。
     */
    @Override
    public ResponseMessage<String> dropCourse(String studentId, Integer courseId, String semesterYear,
            String semesterTime) {
        try {
            // 准备Redis键
            List<String> keys = Arrays.asList(
                    "student:" + studentId + ":courses",
                    "course:" + courseId + ":capacity");
            // 执行原子化退课操作
            Long result = redisTemplate.execute(
                    dropCourseScript,
                    keys,
                    courseId.toString());

            if (result == null) {
                logger.error("Redis 退课脚本执行返回空结果，可能由于连接超时或脚本环境异常: studentId={}, courseId={}", studentId, courseId);
                return ResponseMessage.fail("系统繁忙（Redis异常），请稍后重试");
            }
            switch (result.intValue()) {
                case 0:
                    logger.info("退课失败-未选该课程: studentId={}, courseId={}", studentId, courseId);
                    return ResponseMessage.fail("未选该课程，无法退课");
                case 1:// 退课成功
                    Map<String, Object> msg = new HashMap<>();
                    String dropMsgId = UUID.randomUUID().toString();
                    msg.put("msgId", dropMsgId);
                    msg.put("studentId", studentId);
                    msg.put("courseId", courseId);
                    msg.put("semesterYear", semesterYear);
                    msg.put("semesterTime", semesterTime);
                    msg.put("type", "drop");

                    String queue = "course.drop.queue";
                    // 主方案：RocketMQ 顺序消息发送
                    // 与选课使用相同的 orderKey，保证退课消息进入同一个 Queue，与选课严格顺序
                    String orderKey = studentId + "-" + courseId;
                    try {
                        rocketMQTemplate.syncSendOrderly(
                                ROCKETMQ_TOPIC + ":drop",
                                org.springframework.messaging.support.MessageBuilder.withPayload(msg).build(),
                                orderKey);
                        logger.info("退课请求已顺序提交 (RocketMQ): studentId={}, courseId={}, key={}, msgId={}", studentId, courseId, orderKey, dropMsgId);

                        // 备用方案：RabbitMQ (当前注释)
                        // rabbitTemplate.convertAndSend(queue, msg);

                        return ResponseMessage.success("退课请求已提交，结果稍后可查询");
                    } catch (Exception e) {
                        logger.error("MQ发送异常，回滚退课Redis库存拦截标记: studentId={}, courseId={}", studentId, courseId, e);
                        redisTemplate.opsForSet().add("student:" + studentId + ":courses", courseId.toString());
                        return ResponseMessage.fail("系统繁忙，提交退课请求失败，请稍后重试");
                    }
                default:
                    throw new RuntimeException("未知的脚本返回值: " + result);
            }
        } catch (Exception e) {
            logger.error("退课异常: studentId={}, courseId={}, error=", studentId, courseId, e);
            return ResponseMessage.fail("系统异常，请稍后重试");
        }
    }
}
