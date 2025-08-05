package com.example.ZhangDT.service.impl;

import com.example.ZhangDT.bean.exam.Classroom;
import com.example.ZhangDT.mapper.ClassroomMapper;
import com.example.ZhangDT.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassroomServiceImpl implements ClassroomService {
    @Autowired
    private ClassroomMapper classroomMapper;

    @Override
    public List<Classroom> getAllclassroom() {
        return classroomMapper.findAll();
    }

    @Override
    public Classroom getById(Integer classroomId) {
        return classroomMapper.findById(classroomId);
    }

    @Override
    public Classroom add(Classroom classroom) {
        int res = classroomMapper.insert(classroom);
        return res > 0 ? classroom : null;
    }

    @Override
    public Classroom update(Classroom classroom) {
        int result = classroomMapper.updateById(classroom);
        if (result > 0) {
            return classroomMapper.selectById(classroom.getClassroomId());
        } else {
            return null;
        }
    }

    @Override
    public boolean delete(Integer classroomId) {
        return classroomMapper.deleteById(classroomId) > 0;
    }
} 