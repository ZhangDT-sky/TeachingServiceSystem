package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.CourseOffer;
import com.example.ZhangDT.bean.dto.CourseOfferingDTO;
import java.util.List;
 
public interface CourseOfferingService {
    List<CourseOfferingDTO> getOfferingsByMajorId(Integer majorId);
    List<CourseOfferingDTO> getOfferingsByStudentId(Integer studentId);
    List<CourseOfferingDTO> getOfferingsBySemesterCodeAndMajorCode(String semesterCode, String majorCode);
    // 增删改查
    CourseOffer add(CourseOffer courseOffer);
    void delete(Integer courseId, Integer majorId, Integer semesterId);
    CourseOffer update(CourseOffer courseOffer);
    CourseOffer getByIds(Integer courseId, Integer majorId, Integer semesterId);
    List<CourseOffer> getAll();
} 