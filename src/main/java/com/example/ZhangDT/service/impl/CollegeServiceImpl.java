package com.example.ZhangDT.service.impl;

import com.alibaba.fastjson2.JSON;
import com.example.ZhangDT.bean.College;
import com.example.ZhangDT.mapper.CollegeMapper;
import com.example.ZhangDT.service.CollegeService;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CollegeServiceImpl implements CollegeService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    private static final String COLLEGE_LIST_KEY = "college:list";
    private static final String COLLEGE_LOCK_KEY = "lock:college";

    /**
     * 获取所有学院信息（带缓存保护）
     * 解决问题：
     * 1. 缓存击穿：多线程并发查询过期 Key 时，只允许一个线程查库。
     * 2. 双重检查锁 (DCL)：获取锁后再次校验缓存，避免重复查库。
     */
    @Override
    public List getAllcollege() {
        // 尝试从缓存获取
        String json = redisTemplate.opsForValue().get(COLLEGE_LIST_KEY);
        if (json != null) {
            return JSON.parseArray(json, College.class);
        }

        // 缓存未命中，获取分布式读写锁
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(COLLEGE_LOCK_KEY);
        RLock readLock = rwLock.readLock();

        try {
            // 尝试加读锁（共享锁）：允许多个线程并行读取，但会阻塞写锁
            // 等待时间 10s，持有锁时间 30s
            if (readLock.tryLock(10, 30, TimeUnit.SECONDS)) {
                // 【Double-Checked Locking】再次检查缓存，可能前一个拿锁的线程已经缓存好了
                json = redisTemplate.opsForValue().get(COLLEGE_LIST_KEY);
                if (json != null) {
                    return JSON.parseArray(json, College.class);
                }

                // 查询数据库
                List<College> list = collegeMapper.selectList(null);
                // 缓存 6 小时，防止缓存雪崩
                redisTemplate.opsForValue().set(COLLEGE_LIST_KEY, JSON.toJSONString(list), 6, TimeUnit.HOURS);
                return list;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // 务必释放锁
            readLock.unlock();
        }

        return collegeMapper.selectList(null);
    }

    @Override
    public College getCollegebyid(Integer id) {
        String key="college:byid:"+id;
        String json =redisTemplate.opsForValue().get(key);
        if(json!=null){
            return JSON.parseObject(json, College.class);
        }
        College college=collegeMapper.selectById(id);
        if(college!=null){
            return college;
        }
        return null;
    }

    /**
     * 添加学院（写锁保障一致性）
     */
    @Override
    public College add(College college) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(COLLEGE_LOCK_KEY);
        RLock writeLock = rwLock.writeLock();
        try {
            // 获取写锁（排他锁）：阻止所有读写请求，直到更新完成
            writeLock.lock();
            int result = collegeMapper.insert(college);
            if (result > 0) {
                // 简单失效模式：删除缓存
                redisTemplate.delete(COLLEGE_LIST_KEY);
                return college;
            }
        } finally {
            writeLock.unlock();
        }
        return null;
    }

    /**
     * 删除学院（写锁保障一致性）
     */
    @Override
    public College delete(Integer id) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(COLLEGE_LOCK_KEY);
        RLock writeLock = rwLock.writeLock();
        try {
            writeLock.lock();
            College college = collegeMapper.selectById(id);
            if (college != null) {
                int result = collegeMapper.deleteById(id);
                if (result > 0) {
                    // 同时删除列表缓存和明细缓存
                    redisTemplate.delete(COLLEGE_LIST_KEY);
                    redisTemplate.delete("college:byid:" + id);
                    return college;
                }
            }
        } finally {
            writeLock.unlock();
        }
        return null;
    }

    /**
     * 更新学院（写锁保障一致性）
     */
    @Override
    public College update(College college) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(COLLEGE_LOCK_KEY);
        RLock writeLock = rwLock.writeLock();
        try {
            writeLock.lock();
            int result = collegeMapper.updateById(college);
            if (result > 0) {
                redisTemplate.delete(COLLEGE_LIST_KEY);
                redisTemplate.delete("college:byid:" + college.getCollegeId());
                return collegeMapper.selectById(college.getCollegeId());
            }
        } finally {
            writeLock.unlock();
        }
        return null;
    }
}
