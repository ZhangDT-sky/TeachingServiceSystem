package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.Semester;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/semester")
public class SemesterController {

    @Autowired
    private SemesterService semesterService;

    @GetMapping("/list")
    public ResponseMessage<List<Semester>> getAllSemesters() {
        return ResponseMessage.success(semesterService.getAllSemesters());
    }

    @GetMapping("/{id}")
    public ResponseMessage<Semester> getSemesterById(@PathVariable Integer id) {
        return ResponseMessage.success(semesterService.getSemesterById(id));
    }

    @PostMapping("/add")
    public ResponseMessage<Semester> addSemester(@RequestBody Semester semester) {
        return ResponseMessage.success(semesterService.addSemester(semester));
    }

    @PutMapping("/update")
    public ResponseMessage<Semester> updateSemester(@RequestBody Semester semester) {
        return ResponseMessage.success(semesterService.updateSemester(semester));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage<Semester> deleteSemester(@PathVariable Integer id) {
        return ResponseMessage.success(semesterService.deleteSemester(id));
    }
} 