package com.example.ZhangDT.service.impl;

import com.example.ZhangDT.bean.Student;
import com.example.ZhangDT.bean.dto.StudentDTO;
import com.example.ZhangDT.mapper.StudentMapper;
import com.example.ZhangDT.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public List<StudentDTO> getAllstudent() {
        logger.info("调用getAllstudent方法");
        List<StudentDTO> result = studentMapper.selectStudentDTOList();
        logger.info("getAllstudent返回结果: {}", result);
        return result;
    }

    @Override
    public StudentDTO getStudentByid(String studentId) {
        logger.info("调用getStudentByid方法, studentId={}", studentId);
        StudentDTO result = studentMapper.selectStudentDTOById(studentId);
        logger.info("getStudentByid返回结果: {}", result);
        return result;
    }

    @Override
    public Student add(Student student) {
        int result=studentMapper.insert(student);
        if(result==0){
            return null;
        }
        return student;
    }

    @Override
    public void delete(String Studentid){
        studentMapper.deleteById(Studentid);
    }

    @Override
    public Student update(Student student) {
        System.out.println(student.getStudentId()+"=====");
        int result = studentMapper.updateById(student);
        if (result > 0) {
            return studentMapper.selectById(student.getStudentId());
        } else {
            return null;
        }
    }


}
