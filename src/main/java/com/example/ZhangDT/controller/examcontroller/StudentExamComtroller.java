package com.example.ZhangDT.controller.examcontroller;

import com.example.ZhangDT.bean.exam.StudentExam;
import com.example.ZhangDT.service.StudentExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/studentExam")
public class StudentExamComtroller {
    @Autowired
    private StudentExamService studentExamService;

    @GetMapping("/myExams/{studentId}")
    public List<StudentExam> getMyExams(@PathVariable String studentId) {
        return studentExamService.getExamsByStudentId(studentId);
    }

    @GetMapping("/byExam/{examId}")
    public List<StudentExam> getByExam(@PathVariable Integer examId) {
        return studentExamService.getExamsByExamId(examId);
    }

    @GetMapping("/list")
    public List<StudentExam> getAll() {
        return studentExamService.getAll();
    }

    @PostMapping("/add")
    public StudentExam add(@RequestBody StudentExam studentExam) {
        return studentExamService.add(studentExam);
    }

    @PutMapping("/update")
    public StudentExam update(@RequestBody StudentExam studentExam) {
        return studentExamService.update(studentExam);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestParam String studentId, @RequestParam Integer examId) {
        return studentExamService.delete(studentId, examId);
    }
}
