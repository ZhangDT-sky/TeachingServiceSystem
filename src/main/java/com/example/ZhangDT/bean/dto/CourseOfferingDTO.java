package com.example.ZhangDT.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseOfferingDTO {
    private Integer courseId;
    private String courseName;
    private Integer majorId;
    private String majorName;
    private Integer semesterId;
    private String semesterYear;
    private Integer semesterTime;
    // 可扩展其他字段，如教师、容量等
} 