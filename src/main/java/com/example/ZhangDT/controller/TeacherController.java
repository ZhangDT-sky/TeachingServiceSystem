package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.Teacher;
import com.example.ZhangDT.bean.dto.TeacherDTO;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    TeacherService teacherService;

    @GetMapping("/list")
    public ResponseMessage<List<Teacher>> getlist(){
        List<Teacher> list=teacherService.getAllteacher();
        return ResponseMessage.success(list);
    }

    @GetMapping({"/{teacherId}"})
    public ResponseMessage<Teacher> getTeacherByid(@PathVariable Integer teacherId){
        Teacher teacherNew=teacherService.getTeacherByid(teacherId);
        return ResponseMessage.success(teacherNew);
    }

    @PostMapping("/add")
    public ResponseMessage<Teacher> add(@RequestBody @Validated Teacher teacher){
        Teacher teacherNew=teacherService.add(teacher);
        return ResponseMessage.success(teacherNew);
    }

    @DeleteMapping("/delete")
    public void delete(@PathVariable Integer teacherId){
        teacherService.delete(teacherId);
    }

    @PutMapping("/update")
    public ResponseMessage<Teacher> update(@PathVariable Teacher teacher){
        Teacher teacherNew=teacherService.update(teacher);
        if (teacherNew!=null){return ResponseMessage.success(teacherNew);}
        return ResponseMessage.fail("修改失败");
    }

    // 根据学院ID查询该学院所有教师
    @GetMapping("/college/{collegeName}")
    public ResponseMessage<List<TeacherDTO>> getTeachersByCollegeName(@PathVariable String collegeName){
        List<TeacherDTO> list = teacherService.getTeachersByCollegeName(collegeName);
        return ResponseMessage.success(list);
    }

    // 根据课程名称查询相关教师
    @GetMapping("/course/{courseName}")
    public ResponseMessage<List<TeacherDTO>> getTeachersByCourseName(@PathVariable String courseName){
        List<TeacherDTO> list = teacherService.getTeachersByCourseName(courseName);
        return ResponseMessage.success(list);
    }
}
