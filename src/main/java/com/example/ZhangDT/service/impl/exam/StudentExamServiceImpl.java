package com.example.ZhangDT.service.impl.exam;

import com.example.ZhangDT.bean.exam.StudentExam;
import com.example.ZhangDT.mapper.StudentExamMapper;
import com.example.ZhangDT.service.StudentExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentExamServiceImpl implements StudentExamService {
    @Autowired
    private StudentExamMapper studentExamMapper;

    @Override
    public List<StudentExam> getExamsByStudentId(String studentId) {
        return studentExamMapper.findByStudentId(studentId);
    }

    @Override
    public List<StudentExam> getExamsByExamId(Integer examId) {
        return studentExamMapper.findByExamId(examId);
    }

    @Override
    public List<StudentExam> getAll() {
        return studentExamMapper.findAll();
    }

    @Override
    public StudentExam add(StudentExam studentExam) {
        int res = studentExamMapper.insert(studentExam);
        return res > 0 ? studentExam : null;
    }

    @Override
    public StudentExam update(StudentExam studentExam) {
        int res = studentExamMapper.update(studentExam);
        return res > 0 ? studentExam : null;
    }

    @Override
    public boolean delete(String studentId, Integer examId) {
        return studentExamMapper.delete(studentId, examId) > 0;
    }
} 