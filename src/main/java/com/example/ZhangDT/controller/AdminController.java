package com.example.ZhangDT.controller;


import com.example.ZhangDT.bean.Admin;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.Cache;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @GetMapping("/{AdminId}")
    public ResponseMessage<Admin> getAdminbyId(@PathVariable Integer AdminId) {
        Admin adminNew= adminService.getAdminbyid(AdminId);
        if (adminNew == null) {return ResponseMessage.fail("查询失败");}
        return ResponseMessage.success(adminNew);
    }

    @GetMapping("/list")
    public ResponseMessage<List<Admin>> getAllAdmin() {
        List<Admin> list=adminService.getAllAdmin();
        return ResponseMessage.success(list);
    }

    @PostMapping("/add")
    public ResponseMessage<Admin> add(@Validated @RequestBody Admin admin){
        Admin adminNew = adminService.add(admin);
        if(adminNew==null){return ResponseMessage.fail("插入失败");}
        return ResponseMessage.success(adminNew);
    }

    @DeleteMapping("/delete")
    public ResponseMessage<Admin> delete(@PathVariable Integer adminId){
        adminService.delete(adminId);
        return ResponseMessage.success();
    }

    @PutMapping("/update")
    public ResponseMessage<Admin> update(@RequestBody Admin admin){
        Admin adminNew=adminService.update(admin);
        if(adminNew!=null){return ResponseMessage.success(adminNew);}
        return ResponseMessage.fail("修改失败");
    }

}
