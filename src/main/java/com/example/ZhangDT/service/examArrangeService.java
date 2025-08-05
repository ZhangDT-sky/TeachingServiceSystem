package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.exam.Classroom;
import com.example.ZhangDT.bean.exam.Exam;
import com.example.ZhangDT.bean.exam.ExamSchedule;
import com.example.ZhangDT.bean.exam.StudentExam;

import java.util.List;

public interface examArrangeService {

    List<ExamSchedule> arrangeExams(List<Exam> exams,
                                    List<StudentExam> studentExams ,
                                    List<Classroom> classrooms);

}
