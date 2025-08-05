package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.CourseSelect;
import com.example.ZhangDT.bean.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface CourseSelectService {
    // 查询某专业某学年学期可选课程
    List<Course> getAvailableCourses(Integer majorId, String semesterYear, Integer semesterTime);

    boolean save(CourseSelect courseSelect);

    void insert(CourseSelect courseSelect);
}