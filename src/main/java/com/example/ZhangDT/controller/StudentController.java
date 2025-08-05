package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.Student;
import com.example.ZhangDT.bean.dto.StudentDTO;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentService studentService;

    @GetMapping("/list")
    public ResponseMessage<List<StudentDTO>> getAllstudent() {
        List<StudentDTO> list=studentService.getAllstudent();
        return ResponseMessage.success(list);
    }

    @GetMapping("/{studentId}")
    public ResponseMessage<StudentDTO> getStudentByid(@PathVariable String studentId) {
        StudentDTO studentNew=studentService.getStudentByid(studentId);
        return ResponseMessage.success(studentNew);
    }

    @PostMapping("/add")
    public ResponseMessage<Student> add(@Validated @RequestBody Student student){
        Student studentNew = studentService.add(student);
        if(studentNew!=null){return ResponseMessage.success(studentNew);}
        return ResponseMessage.fail("插入失败");
    }

    @DeleteMapping("/delete")
    public void delete(@PathVariable String studentId){
        studentService.delete(studentId);
    }

    @PutMapping("/update")
    public ResponseMessage<Student> update(@Validated @RequestBody Student student){
        Student studentNew=studentService.update(student);
        if(studentNew!=null){return ResponseMessage.success(studentNew);}
        return ResponseMessage.fail("修改失败");
    }


}
