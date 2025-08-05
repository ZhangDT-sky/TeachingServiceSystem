package com.example.ZhangDT.service.impl;

import com.alibaba.fastjson2.JSON;
import com.example.ZhangDT.bean.College;
import com.example.ZhangDT.mapper.CollegeMapper;
import com.example.ZhangDT.service.CollegeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CollegeServiceImpl implements CollegeeService {

    @Autowired
    CollegeMapper collegeMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public List getAllcollege() {
        String key="college:list";
        String json =redisTemplate.opsForValue().get(key);
        if(json!=null){
            //使用fastjson
            return JSON.parseArray(json, College.class);
        }
        List<College> list=collegeMapper.selectList(null);
        redisTemplate.opsForValue().set(key,JSON.toJSONString(list),6, TimeUnit.HOURS);
        return list;
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

    @Override
    public College add(College college) {
        int result = collegeMapper.insert(college);
        if (result > 0) {
            redisTemplate.delete("college:list");
            return college;
        }
        return null;
    }

    @Override
    public College delete(Integer id) {
        College college = collegeMapper.selectById(id);
        if (college != null) {
            int result = collegeMapper.deleteById(id);
            if (result > 0) {
                redisTemplate.delete("college:list");
                redisTemplate.delete("college:byid:" + id);
                return college;
            }
        }
        return null;
    }

    @Override
    public College update(College college) {
        int result = collegeMapper.updateById(college);
        if (result > 0) {
            redisTemplate.delete("college:list");
            redisTemplate.delete("college:byid:"+college.getCollegeId());
            return collegeMapper.selectById(college.getCollegeId());
        }
        return null;
    }
}
