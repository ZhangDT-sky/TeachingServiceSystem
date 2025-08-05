package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.Class;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class")
public class ClassController {
    @Autowired
    private ClassService classService;

    @GetMapping("/{classId}")
    public ResponseMessage<Class> getClassById(@PathVariable Integer classId) {
        Class clazz = classService.getClassById(classId);
        if (clazz == null) {return ResponseMessage.fail("查询失败");}
        return ResponseMessage.success(clazz);
    }

    @GetMapping("/list")
    public ResponseMessage<List<Class>> getAllClass() {
        List<Class> list = classService.getAllClass();
        return ResponseMessage.success(list);
    }

    @PostMapping("/add")
    public ResponseMessage<Class> add(@Validated @RequestBody Class clazz) {
        Class newClazz = classService.add(clazz);
        if (newClazz == null) {return ResponseMessage.fail("插入失败");}
        return ResponseMessage.success(newClazz);
    }

    @DeleteMapping("/delete/{classId}")
    public ResponseMessage<Void> delete(@PathVariable Integer classId) {
        classService.delete(classId);
        return ResponseMessage.success();
    }

    @PutMapping("/update")
    public ResponseMessage<Class> update(@Validated @RequestBody Class clazz) {
        Class updated = classService.update(clazz);
        if (updated != null) {return ResponseMessage.success(updated);}
        return ResponseMessage.fail("修改失败");
    }
}
