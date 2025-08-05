package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.Teacher;
import com.example.ZhangDT.bean.dto.TeacherDTO;

import java.util.List;

public interface TeacherService {
    List<Teacher> getAllteacher();

    Teacher getTeacherByid(Integer teacherId);

    Teacher add(Teacher teacher);

    void delete(Integer teacherId);

    Teacher update(Teacher teacher);

    
    List<TeacherDTO> getTeachersByCourseName(String courseName);

    List<TeacherDTO> getTeachersByCollegeName(String  collegeName);
}
