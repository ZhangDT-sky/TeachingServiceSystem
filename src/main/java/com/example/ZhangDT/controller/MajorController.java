package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.Major;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/major")
public class MajorController {
    @Autowired
    private MajorService majorService;

    @GetMapping("/list")
    public ResponseMessage<List<Major>> getAllMajors() {
        return ResponseMessage.success(majorService.getAllMajors());
    }

    @GetMapping("/{majorId}")
    public ResponseMessage<Major> getMajorById(@PathVariable Integer majorId) {
        return ResponseMessage.success(majorService.getMajorById(majorId));
    }

    @PostMapping("/add")
    public ResponseMessage<Major> add(@RequestBody Major major) {
        Major m = majorService.add(major);
        return ResponseMessage.success(m);
    }

    @DeleteMapping("/delete/{majorId}")
    public ResponseMessage<Major> delete(@PathVariable Integer majorId) {
        Major deletedMajor = majorService.delete(majorId);
        if (deletedMajor == null) {
            return ResponseMessage.fail("删除失败或专业不存在");
        }
        return ResponseMessage.success(deletedMajor);
    }

    @PutMapping("/update")
    public ResponseMessage<Major> update(@RequestBody Major major) {
        Major m = majorService.update(major);
        return ResponseMessage.success(m);
    }

    @GetMapping("/count/{majorId}")
    public ResponseMessage<Integer> countStudentsByMajorId(@PathVariable Integer majorId) {
        int count = majorService.countStudentsByMajorId(majorId);
        return ResponseMessage.success(count);
    }
} 