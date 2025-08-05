package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.Course;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/list")
    public ResponseMessage<List<Course>> getAllCourses() {
        return ResponseMessage.success(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseMessage<Course> getCourseById(@PathVariable Integer id) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return ResponseMessage.fail("未找到该课程");
        }
        return ResponseMessage.success(course);
    }

    @PostMapping("/add")
    public ResponseMessage<Course> addCourse(@Validated @RequestBody Course course) {
        Course newCourse = courseService.addCourse(course);
        if (newCourse == null) {
            return ResponseMessage.fail("添加课程失败");
        }
        return ResponseMessage.success(newCourse);
    }

    @PutMapping("/update")
    public ResponseMessage<Course> updateCourse(@Validated @RequestBody Course course) {
        Course updated = courseService.updateCourse(course);
        if (updated == null) {
            return ResponseMessage.fail("修改课程失败");
        }
        return ResponseMessage.success(updated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage<Course> deleteCourse(@PathVariable Integer id) {
        Course deleted = courseService.deleteCourse(id);
        if (deleted == null) {
            return ResponseMessage.fail("删除课程失败");
        }
        return ResponseMessage.success(deleted);
    }
}
