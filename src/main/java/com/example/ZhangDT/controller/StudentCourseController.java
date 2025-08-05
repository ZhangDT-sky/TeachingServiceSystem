package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.StudentCourse;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.StudentCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studentcourse")
public class StudentCourseController {

    @Autowired
    private StudentCourseService studentCourseService;

    @PostMapping("/select")
    public ResponseMessage<String> selectCourse(@RequestBody StudentCourse sc) {
        return studentCourseService.selectCourse(sc.getStudentId(),sc.getCourseId(),sc.getSemesterYear(),String.valueOf(sc.getSemesterTime()));
    }

    @PostMapping("/drop")
    public ResponseMessage<String> dropCourse(@RequestBody StudentCourse sc) {
        return studentCourseService.dropCourse(sc.getStudentId(),sc.getCourseId(),sc.getSemesterYear(),String.valueOf(sc.getSemesterTime()));
    }

}
