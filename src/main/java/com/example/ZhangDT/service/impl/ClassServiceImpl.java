package com.example.ZhangDT.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ZhangDT.bean.Class;
import com.example.ZhangDT.mapper.ClassMapper;
import com.example.ZhangDT.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {
    @Autowired
    private ClassMapper classMapper;

    @Override
    public Class getClassById(Integer classId) {
        return classMapper.selectById(classId);
    }

    @Override
    public List<Class> getAllClass() {
        return classMapper.selectList(null);
    }

    @Override
    public Class add(Class clazz) {
        int result = classMapper.insert(clazz);
        return result > 0 ? clazz : null;
    }

    @Override
    public void delete(Integer classId) {
        classMapper.deleteById(classId);
    }

    @Override
    public Class update(Class clazz) {
        int result = classMapper.updateById(clazz);
        if (result > 0) {
            return classMapper.selectById(clazz.getClassId());
        } else {
            return null;
        }
    }
} 