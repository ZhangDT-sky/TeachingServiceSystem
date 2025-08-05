package com.example.ZhangDT.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ZhangDT.bean.Course;
import com.example.ZhangDT.mapper.CourseMapper;
import com.example.ZhangDT.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Course> getAllCourses() {
        return courseMapper.selectList(null);
    }

    @Override
    public Course getCourseById(Integer courseId) {
        return courseMapper.selectById(courseId);
    }

    @Override
    public Course addCourse(Course course) {
        int result = courseMapper.insert(course);
        return result > 0 ? course : null;
    }

    @Override
    public Course updateCourse(Course course) {
        int result = courseMapper.updateById(course);
        if (result > 0) {
            return courseMapper.selectById(course.getCourseId());
        } else {
            return null;
        }
    }

    @Override
    public Course deleteCourse(Integer courseId) {
        Course course = courseMapper.selectById(courseId);
        int result = courseMapper.deleteById(courseId);
        return result > 0 ? course : null;
    }
} 