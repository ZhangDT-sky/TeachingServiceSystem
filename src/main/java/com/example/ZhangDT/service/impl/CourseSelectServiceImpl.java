package com.example.ZhangDT.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ZhangDT.bean.CourseSelect;
import com.example.ZhangDT.bean.Course;
import com.example.ZhangDT.mapper.CourseSelectMapper;
import com.example.ZhangDT.mapper.SemesterMapper;
import com.example.ZhangDT.service.CourseSelectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseSelectServiceImpl extends ServiceImpl<CourseSelectMapper, CourseSelect> implements CourseSelectService {

    @Autowired
    private CourseSelectMapper courseSelectMapper;


    @Override
    public List<Course> getAvailableCourses(Integer majorId, String semesterYear, Integer semesterTime) {
        // 通过自定义SQL或MyBatis-Plus查询可选课程
        return courseSelectMapper.getAvailableCourses(majorId,semesterYear,semesterTime);
    }

    @Override
    public boolean save(CourseSelect courseSelect){
        return courseSelectMapper.save(courseSelect.getCourseId());
    }

    @Override
    public void insert(CourseSelect courseSelect) {
        courseSelectMapper.insert(courseSelect);
    }
} 