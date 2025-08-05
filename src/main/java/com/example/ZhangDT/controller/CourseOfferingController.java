package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.CourseOffer;
import com.example.ZhangDT.bean.dto.CourseOfferingDTO;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.CourseOfferingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/courseOffering")
public class CourseOfferingController {
    @Autowired
    private CourseOfferingService courseOfferingService;

    // 根据专业ID查询该专业所有课程开设及学期信息
    @GetMapping("/major/{majorId}")
    public ResponseMessage<List<CourseOfferingDTO>> getByMajorId(@PathVariable Integer majorId) {
        List<CourseOfferingDTO> list = courseOfferingService.getOfferingsByMajorId(majorId);
        return ResponseMessage.success(list);
    }

    // 根据学生ID查询其所属专业的所有课程开设及学期信息
    @GetMapping("/student/{studentId}")
    public ResponseMessage<List<CourseOfferingDTO>> getByStudentId(@PathVariable Integer studentId) {
        List<CourseOfferingDTO> list = courseOfferingService.getOfferingsByStudentId(studentId);
        return ResponseMessage.success(list);
    }

    // 根据学期代码和专业代码查询课程代码
    @GetMapping("/semester/{semesterCode}/major/{majorCode}")
    public ResponseMessage<List<CourseOfferingDTO>> getBySemesterCodeAndMajorCode(
            @PathVariable String semesterCode, 
            @PathVariable String majorCode) {
        List<CourseOfferingDTO> list = courseOfferingService.getOfferingsBySemesterCodeAndMajorCode(semesterCode, majorCode);
        return ResponseMessage.success(list);
    }

    // 新增
    @PostMapping("/add")
    public ResponseMessage<CourseOffer> add(@Validated @RequestBody CourseOffer courseOffer) {
        CourseOffer newOffer = courseOfferingService.add(courseOffer);
        if(newOffer == null) { return ResponseMessage.fail("插入失败"); }
        return ResponseMessage.success(newOffer);
    }

    // 删除
    @DeleteMapping("/delete")
    public ResponseMessage<Void> delete(@RequestParam Integer courseId, @RequestParam Integer majorId, @RequestParam Integer semesterId) {
        courseOfferingService.delete(courseId, majorId, semesterId);
        return ResponseMessage.success();
    }

    // 修改
    @PutMapping("/update")
    public ResponseMessage<CourseOffer> update(@RequestBody CourseOffer courseOffer) {
        CourseOffer updated = courseOfferingService.update(courseOffer);
        if(updated != null) { return ResponseMessage.success(updated); }
        return ResponseMessage.fail("修改失败");
    }

    // 查询单条
    @GetMapping("/get")
    public ResponseMessage<CourseOffer> getByIds(@RequestParam Integer courseId, @RequestParam Integer majorId, @RequestParam Integer semesterId) {
        CourseOffer offer = courseOfferingService.getByIds(courseId, majorId, semesterId);
        if(offer == null) { return ResponseMessage.fail("未找到"); }
        return ResponseMessage.success(offer);
    }

    // 查询全部
    @GetMapping("/list")
    public ResponseMessage<List<CourseOffer>> getAll() {
        List<CourseOffer> list = courseOfferingService.getAll();
        return ResponseMessage.success(list);
    }
} 