package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.exam.StudentExam;
import java.util.List;

public interface StudentExamService {
    List<StudentExam> getExamsByStudentId(String studentId);
    List<StudentExam> getExamsByExamId(Integer examId);
    List<StudentExam> getAll();
    StudentExam add(StudentExam studentExam);
    StudentExam update(StudentExam studentExam);
    boolean delete(String studentId, Integer examId);
} 