package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.exam.Exam;
import com.example.ZhangDT.bean.exam.ExamSchedule;
import com.example.ZhangDT.bean.exam.StudentExam;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

public interface ExamService {
    Exam addExam(Exam exam);
    List<Exam> getAllExams();
    Exam getExamById(Integer examId);


    List<StudentExam> getExamScheduleByexamid(List<Integer> examIds);

    List<Exam> getlistByexamid(List<Integer> examIds);

    List<StudentExam> assign(List<ExamSchedule> result);

    void deleteList(List<Integer> examIds);
}