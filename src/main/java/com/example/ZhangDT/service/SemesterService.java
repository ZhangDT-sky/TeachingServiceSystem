package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.Semester;
import java.util.List;

public interface SemesterService {
    List<Semester> getAllSemesters();
    Semester getSemesterById(Integer semesterId);
    Semester addSemester(Semester semester);
    Semester updateSemester(Semester semester);
    Semester deleteSemester(Integer semesterId);
} 