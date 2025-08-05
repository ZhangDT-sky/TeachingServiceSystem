package com.example.ZhangDT.service;


import com.example.ZhangDT.bean.Grade;
import com.example.ZhangDT.bean.dto.GradeDTO;

import java.util.List;

public interface GradeService {
    GradeDTO getGradebyid(Integer gradeId);

    List<GradeDTO> getAllGrade();

    Grade add(Grade admin);

    void delete(Integer adminId);

    Grade update(Grade admin);

    List<Grade> getStudentgrade(String studentId);

    /**
     * 计算学生的加权平均绩点（GPA）
     */
    double calculateGpaFromRedis(String studentId);
}
