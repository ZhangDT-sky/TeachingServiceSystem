package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.Course;
import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    Course getCourseById(Integer courseId);
    Course addCourse(Course course);
    Course updateCourse(Course course);
    Course deleteCourse(Integer courseId);
} 