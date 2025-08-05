package com.example.ZhangDT.service.impl;

import com.alibaba.fastjson2.JSON;
import com.example.ZhangDT.bean.Major;
import com.example.ZhangDT.mapper.MajorMapper;
import com.example.ZhangDT.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MajorServiceImpl implements MajorService {
    @Autowired
    private MajorMapper majorMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<Major> getAllMajors() {
        String key="Major:list";
        String json=stringRedisTemplate.opsForValue().get(key);
        if(json!=null){
            return JSON.parseArray(json,Major.class);
        }
        List<Major> list=majorMapper.selectList(null);
        stringRedisTemplate.opsForValue().set(key,JSON.toJSONString(list),6, TimeUnit.HOURS);
        return list;
    }

    @Override
    public Major getMajorById(Integer majorId) {
        String key="Major:MajorId"+majorId;
        String json=stringRedisTemplate.opsForValue().get(key);
        if(json!=null){
            return JSON.parseObject(json,Major.class);
        }
        Major major=majorMapper.selectById(majorId);
        if(major!=null){
            return major;
        }
        return null;
    }

    @Override
    public Major add(Major major) {
        int result=majorMapper.insert(major);
        if(result>0){
            stringRedisTemplate.delete("Major:list");
            return major;
        }
        return null;
    }

    @Override
    public Major delete(Integer majorId) {
        Major major = majorMapper.selectById(majorId);
        if (major != null) {
            int result = majorMapper.deleteById(majorId);
            if (result > 0) {
                String key = "Major:MajorId" + majorId;
                stringRedisTemplate.delete("Major:list");
                stringRedisTemplate.delete(key);
                return major;
            }
        }
        return null;
    }

    @Override
    public Major update(Major major) {
        String key="Major:MajorId"+major.getMajorId();
        stringRedisTemplate.delete("Major:list");
        stringRedisTemplate.delete(key);
        majorMapper.updateById(major);
        return majorMapper.selectById(major.getMajorId());
    }

    @Override
    public int countStudentsByMajorId(Integer majorId) {
        return majorMapper.countStudentsByMajorId(majorId);
    }
} 