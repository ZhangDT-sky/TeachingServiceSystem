package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.Student;
import com.example.ZhangDT.bean.dto.StudentDTO;

import java.util.List;

public interface StudentService {
    List<StudentDTO> getAllstudent();

    StudentDTO getStudentByid(String studentId);

    Student add(Student student);

    void delete(String studentId);

    Student update(Student student);
}
