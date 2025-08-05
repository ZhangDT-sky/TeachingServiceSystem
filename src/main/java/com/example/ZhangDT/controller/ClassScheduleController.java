package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.ClassSchedule;
import com.example.ZhangDT.bean.exam.ArrangeRequest;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.ClassScheduleService;
import com.example.ZhangDT.service.impl.CourseAssignServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/classSchedule")
public class ClassScheduleController {
    @Autowired
    private ClassScheduleService classScheduleService;
    @Autowired
    private CourseAssignServiceImpl courseAssignService;

    @GetMapping("/{id}")
    public ResponseMessage<ClassSchedule> getById(@PathVariable Integer id) {
        ClassSchedule cs = classScheduleService.getById(id);
        if (cs == null) {return ResponseMessage.fail("查询失败");}
        return ResponseMessage.success(cs);
    }

    @GetMapping("/list")
    public ResponseMessage<List<ClassSchedule>> getAll() {
        List<ClassSchedule> list = classScheduleService.getAll();
        return ResponseMessage.success(list);
    }

    @PostMapping("/add")
    public ResponseMessage<ClassSchedule> add(@Validated @RequestBody ClassSchedule classSchedule) {
        ClassSchedule newCS = classScheduleService.add(classSchedule);
        if (newCS == null) {return ResponseMessage.fail("插入失败");}
        return ResponseMessage.success(newCS);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage<Void> delete(@PathVariable Integer id) {
        classScheduleService.delete(id);
        return ResponseMessage.success();
    }

    @PutMapping("/update")
    public ResponseMessage<ClassSchedule> update(@Validated @RequestBody ClassSchedule classSchedule) {
        ClassSchedule updated = classScheduleService.update(classSchedule);
        if (updated != null) {return ResponseMessage.success(updated);}
        return ResponseMessage.fail("修改失败");
    }

    @PostMapping("/assign")
    public ResponseMessage<List<ClassSchedule>> assign(ArrangeRequest arrangeRequest) {
        List<ClassSchedule> list = courseAssignService.assign(arrangeRequest.getMajorIds());
        return ResponseMessage.success(list);
    }
}
