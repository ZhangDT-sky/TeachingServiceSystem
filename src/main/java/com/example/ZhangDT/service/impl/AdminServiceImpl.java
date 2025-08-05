package com.example.ZhangDT.service.impl;

import com.example.ZhangDT.bean.Admin;
import com.example.ZhangDT.mapper.AdminMapper;
import com.example.ZhangDT.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    AdminMapper adminMapper;

    @Override
    public Admin getAdminbyid(Integer id){
        Admin adminNew=adminMapper.selectById(id);
        return adminNew;
    }

    @Override
    public List<Admin> getAllAdmin() {
        //查询全部
        List<Admin> list=adminMapper.selectList(null);
        return list;
    }

    @Override
    public Admin add(Admin admin) {
        int result= adminMapper.insert(admin);
        if(result==0){return null;}
        return admin;
    }

    @Override
    public void delete(Integer adminId) {
        adminMapper.deleteById(adminId);
    }

    @Override
    public Admin update(Admin admin) {
        int result = adminMapper.updateById(admin);
        if (result > 0) {
            return adminMapper.selectById(admin.getAdminId());
        } else {
            return null;
        }
    }

}

