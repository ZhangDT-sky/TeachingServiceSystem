package com.example.ZhangDT.service.impl;

import com.example.ZhangDT.bean.ClassSchedule;
import com.example.ZhangDT.mapper.ClassScheduleMapper;
import com.example.ZhangDT.service.ClassScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassScheduleServiceImpl implements ClassScheduleService {
    @Autowired
    private ClassScheduleMapper classScheduleMapper;

    @Override
    public ClassSchedule getById(Integer id) {
        return classScheduleMapper.selectById(id);
    }

    @Override
    public List<ClassSchedule> getAll() {
        return classScheduleMapper.selectList(null);
    }

    @Override
    public ClassSchedule add(ClassSchedule classSchedule) {
        int result = classScheduleMapper.insert(classSchedule);
        return result > 0 ? classSchedule : null;
    }

    @Override
    public void delete(Integer id) {
        classScheduleMapper.deleteById(id);
    }

    @Override
    public ClassSchedule update(ClassSchedule classSchedule) {
        int result = classScheduleMapper.updateById(classSchedule);
        if (result > 0) {
            return classScheduleMapper.selectById(classSchedule.getId());
        } else {
            return null;
        }
    }
} 