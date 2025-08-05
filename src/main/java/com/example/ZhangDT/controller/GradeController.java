package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.Grade;
import com.example.ZhangDT.bean.dto.GradeDTO;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("grade")
public class GradeController {

    @Autowired
    GradeService gradeService;

    @GetMapping("/{gradeId}")
    public ResponseMessage<GradeDTO> getGradebyId(@PathVariable Integer GradeId) {
        GradeDTO gradeNew= gradeService.getGradebyid(GradeId);
        return ResponseMessage.success(gradeNew);
    }

    @GetMapping("/list")
    public ResponseMessage<List<GradeDTO>> getAllGrade() {
        List<GradeDTO> list=gradeService.getAllGrade();
        return ResponseMessage.success(list);
    }


    @GetMapping("/{studentId}")
    public ResponseMessage<List<Grade>> getStudentgrade(@PathVariable String StudentId) {
        List<Grade> list=gradeService.getStudentgrade(StudentId);
        return ResponseMessage.success(list);
    }


    @PostMapping("/add")
    public ResponseMessage<Grade> add(@Validated @RequestBody Grade grade){
        try {
            Grade gradeNew = gradeService.add(grade);
            if (gradeNew == null) {
                return ResponseMessage.fail("添加失败");
            }
            return ResponseMessage.success(gradeNew);
        }
        catch (DuplicateKeyException e) {
            return ResponseMessage.fail("成绩已存在，添加失败");
        }
        catch (Exception e) {
            return ResponseMessage.fail("系统异常"+e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseMessage<Grade> delete(@PathVariable Integer gradeId){
        gradeService.delete(gradeId);
        return ResponseMessage.success();
    }

    @PutMapping("/update")
    public ResponseMessage<Grade> update(@PathVariable Grade grade){
        Grade adminNew=gradeService.update(grade);
        if(adminNew!=null){return ResponseMessage.success(adminNew);}
        return ResponseMessage.fail("修改失败");
    }

}
