package com.example.ZhangDT.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDTO {
    private Integer teacherId;
    private String name;
    private String phone;
    private String email;
    private Integer courseId;
    private String courseName;
    private Integer collegeId;
    private String collegeName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 