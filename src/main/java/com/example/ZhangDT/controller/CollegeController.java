package com.example.ZhangDT.controller;


import com.example.ZhangDT.bean.College;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.CollegeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/college")
public class CollegeController {

    @Autowired
    CollegeeService collegeeService;

    @GetMapping("/list")
    public ResponseMessage<List<College>> getAllCollege(){
        List list = collegeeService.getAllcollege();
        return ResponseMessage.success(list);
    }

    @GetMapping("/{id}")
    public ResponseMessage<College> getCollegebyid(@PathVariable Integer id){
        College college = collegeeService.getCollegebyid(id);
        if(college==null){return ResponseMessage.fail("查询学院失败");}
        return ResponseMessage.success(college);
    }

    @PostMapping("/add")
    public ResponseMessage<College> add(@RequestBody @Validated College college){
        College collegeNew=collegeeService.add(college);
        if(collegeNew==null){return ResponseMessage.fail("添加学院失败");}
        return ResponseMessage.success(collegeNew);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage<College> delete(@PathVariable Integer id){
        College college=collegeeService.delete(id);
        if(college==null){return ResponseMessage.fail("删除学院失败");}
        return ResponseMessage.success(college);
    }

    @PutMapping("/update")
    public ResponseMessage<College> update(@Validated @RequestBody College college){
        College collegeNew=collegeeService.update(college);
        if(collegeNew==null){return ResponseMessage.fail("修改学院失败");}
        return ResponseMessage.success(collegeNew);
    }
}
