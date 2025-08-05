package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.exam.Classroom;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/classroom")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;

    @GetMapping("/list")
    public ResponseMessage<List<Classroom>> getAllclassroom(){
        List<Classroom> list=classroomService.getAllclassroom();
        return ResponseMessage.success(list);
    }

    @GetMapping("/{classroomId}")
    public ResponseMessage<Classroom> getById(@PathVariable Integer classroomId) {
        Classroom classroom = classroomService.getById(classroomId);
        if (classroom == null) return ResponseMessage.fail("未找到教室");
        return ResponseMessage.success(classroom);
    }

    @PostMapping("/add")
    public ResponseMessage<Classroom> add(@RequestBody Classroom classroom) {
        Classroom res = classroomService.add(classroom);
        if (res == null) return ResponseMessage.fail("添加失败");
        return ResponseMessage.success(res);
    }

    @PutMapping("/update")
    public ResponseMessage<Classroom> update(@RequestBody Classroom classroom) {
        Classroom res = classroomService.update(classroom);
        if (res == null) return ResponseMessage.fail("修改失败");
        return ResponseMessage.success(res);
    }

    @DeleteMapping("/delete/{classroomId}")
    public ResponseMessage<?> delete(@PathVariable Integer classroomId) {
        boolean ok = classroomService.delete(classroomId);
        if (!ok) return ResponseMessage.fail("删除失败");
        return ResponseMessage.success();
    }
}
