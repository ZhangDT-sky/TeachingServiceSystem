package com.example.ZhangDT.service.impl;

import com.example.ZhangDT.bean.Teacher;
import com.example.ZhangDT.bean.dto.TeacherDTO;
import com.example.ZhangDT.mapper.TeacherMapper;
import com.example.ZhangDT.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {


    @Autowired
    TeacherMapper teacherMapper;

    @Override
    public List<Teacher> getAllteacher() {
        List<Teacher> list=teacherMapper.selectList(null);
        return list;
    }

    @Override
    public Teacher getTeacherByid(Integer teacherId) {
        Teacher teacherNew=teacherMapper.selectById(teacherId);
        return teacherNew;
    }

    @Override
    public Teacher add(Teacher teacher) {
        int result=teacherMapper.insert(teacher);
        if(result==0){
            return null;
        }
        return teacher;
    }

    @Override
    public void delete(Integer teacherId) {
        teacherMapper.deleteById(teacherId);
    }

    @Override
    public Teacher update(Teacher teacher) {
        int result = teacherMapper.updateById(teacher);
        if (result > 0) {
            return teacherMapper.selectById(teacher.getTeacherId());
        } else {
            return null;
        }
    }

    @Override
    public List<TeacherDTO> getTeachersByCollegeName(String collegeName) {
        return teacherMapper.selectByCollegeName(collegeName);
    }

    @Override
    public List<TeacherDTO> getTeachersByCourseName(String courseName) {
        return teacherMapper.selectByCourseName(courseName);
    }
}
